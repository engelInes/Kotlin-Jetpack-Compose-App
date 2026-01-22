package com.inesengel.travelapp.UI.view.fragments

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.RECEIVER_EXPORTED
import androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED
import androidx.core.content.ContextCompat.registerReceiver
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.inesengel.travelapp.R
import com.inesengel.travelapp.UI.view.activities.AuthActivity
import com.inesengel.travelapp.UI.components.MainDestinationListScreen
import com.inesengel.travelapp.UI.view.utils.navigateTo
import com.inesengel.travelapp.UI.viewmodel.MainDestinationListViewModel
import com.inesengel.travelapp.UI.view.receivers.BatteryBroadcastReceiver
import com.inesengel.travelapp.UI.view.services.ShareDestinationService
import com.inesengel.travelapp.UI.view.services.UserActivityService
import com.inesengel.travelapp.UI.view.utils.Constants.Broadcasts.BATTERY_CHANGED_ACTION
import com.inesengel.travelapp.UI.view.utils.Constants.Broadcasts.BROADCAST_TAG
import com.inesengel.travelapp.UI.view.utils.Constants.Broadcasts.CANCEL_DELAY_COMMAND
import com.inesengel.travelapp.UI.view.utils.Constants.Broadcasts.COMPONENT_PACKAGE_NAME
import com.inesengel.travelapp.UI.view.utils.Constants.Broadcasts.COMPONENT_RECEIVER
import com.inesengel.travelapp.UI.view.utils.Constants.Broadcasts.MY_CUSTOM_ACTION
import com.inesengel.travelapp.UI.view.utils.Constants.Broadcasts.MY_TRAVEL_PERMISSION
import com.inesengel.travelapp.UI.view.utils.Constants.Broadcasts.START_DELAY_COMMAND
import com.inesengel.travelapp.UI.view.utils.Constants.Broadcasts.USER_TRACKING_COMMAND
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.ARG_DESTINATION_ID
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.ARG_DESTINATION_NAME
import com.inesengel.travelapp.UI.view.utils.Constants.UIViews.NOTIFICATION_TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class MainDestinationListFragment : Fragment() {

    private val viewModel: MainDestinationListViewModel by viewModels()
    private val batteryBroadcastReceiver = BatteryBroadcastReceiver()
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            Log.d(
                NOTIFICATION_TAG,
                if (isGranted) "Permission granted" else "Permission denied"
            )
        }
    private lateinit var shareService: ShareDestinationService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        shareService = ShareDestinationService(requireContext())

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MainDestinationListScreen(
                    viewModel = viewModel,
                    onDestinationClicked = { destinationId, destinationName ->
                        val bundle = bundleOf(
                            ARG_DESTINATION_ID to destinationId,
                            ARG_DESTINATION_NAME to destinationName
                        )
                        findNavController().navigate(R.id.action_list_to_detail, bundle)
                    },
                    onSendBroadcast = { sendBroadcast() },
                    onLogout = {
                        goToLoginScreen()
                        requireActivity().finish()
                    },
                    onAddDestination = {
                        findNavController().navigate(R.id.action_to_add_destination)
                    },
                    onUpdate = {
                        val selectedId = viewModel.uiState.value.destinationOptionsMenu
                        viewModel.onCancelMenu()

                        if (selectedId != null) {
                            val bundle = bundleOf(ARG_DESTINATION_ID to selectedId)
                            findNavController().navigate(
                                R.id.action_to_add_destination,
                                bundle
                            )
                        }
                    },
                    onFavoritesClicked = {
                        findNavController().navigate(R.id.action_to_favorite_destinations)
                    },
                    onShareClicked = { destination ->
                        shareService.shareDestination(destination)
                    },
                    onNavigateToProfile = {
                        findNavController().navigate(R.id.profile_fragment)
                    },
                )
            }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadDestinations()
        requestNotificationPermission()
        setupBroadcastReceiverFromOwnApp()
    }

    override fun onPause() {
        super.onPause()
        startTrackingUser(START_DELAY_COMMAND)
    }

    override fun onResume() {
        super.onResume()
        startTrackingUser(CANCEL_DELAY_COMMAND)
    }

    private fun goToLoginScreen() {
        requireActivity().navigateTo(
            destination = AuthActivity::class.java
        )
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun sendBroadcast() {
        val intent = Intent(MY_CUSTOM_ACTION).apply {
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            setComponent(
                ComponentName(
                    COMPONENT_PACKAGE_NAME,
                    COMPONENT_RECEIVER
                )
            )
        }
        requireContext().sendBroadcast(
            intent,
            MY_TRAVEL_PERMISSION
        )

        Log.d(BROADCAST_TAG, "Sent protected broadcast with action: $MY_CUSTOM_ACTION")
    }

    private fun setupBroadcastReceiverFromOwnApp() {
        val intentFilter = IntentFilter(BATTERY_CHANGED_ACTION)

        val listenToBroadcastsFromOtherApps = false
        val receiverFlags = if (listenToBroadcastsFromOtherApps) {
            RECEIVER_EXPORTED
        } else {
            RECEIVER_NOT_EXPORTED
        }

        registerReceiver(
            requireContext(),
            batteryBroadcastReceiver,
            intentFilter,
            receiverFlags
        )
    }

    private fun startTrackingUser(command: String) {
        val intent = Intent(requireContext(), UserActivityService::class.java).apply {
            putExtra(USER_TRACKING_COMMAND, command)
        }
        requireContext().startService(intent)
    }
}