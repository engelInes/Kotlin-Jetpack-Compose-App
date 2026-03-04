package com.inesengel.travelapp.UI.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.tabs.TabLayoutMediator
import com.inesengel.travelapp.R
import com.inesengel.travelapp.UI.binding.adapters.DetailsViewPageAdapter
import com.inesengel.travelapp.UI.viewmodel.DestinationDetailViewModel
import com.inesengel.travelapp.databinding.DestinationDetailsFragmentBinding
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.ARG_DESTINATION_ID
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.ARG_DESTINATION_NAME
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.INVALID_DESTINATION_ID
import com.inesengel.travelapp.UI.view.utils.Constants.UIViews.DESTINATION_DETAILS_ATTRACTION_INDEX
import com.inesengel.travelapp.UI.view.utils.Constants.UIViews.DESTINATION_DETAILS_REVIEW_INDEX
import com.inesengel.travelapp.UI.viewmodel.DestinationTabViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DestinationDetailFragment : Fragment() {

    private var _binding: DestinationDetailsFragmentBinding? = null
    private val binding get() = _binding!!
    private val destinationId: Int by lazy {
        arguments?.getInt(ARG_DESTINATION_ID) ?: INVALID_DESTINATION_ID
    }
    private val destinationName: String? by lazy {
        arguments?.getString(ARG_DESTINATION_NAME)
    }
    private val viewModel: DestinationDetailViewModel by viewModels()
    private val tabViewModel: DestinationTabViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DestinationDetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        if (destinationId == INVALID_DESTINATION_ID) {
            findNavController().popBackStack()
            return
        }

        tabViewModel.setDestinationId(destinationId)
        setupToolbar()
        setupViewPager()
        setupCollectors()
        viewModel.loadDestination(destinationId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.destination.collect { destination ->
                        if (destination != null) {
                            binding.toolbar.title = destination.destination.name
                        }
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.apply {
                            loadingProgressBar.visibility =
                                if (isLoading) View.VISIBLE else View.GONE

                            viewPager.visibility =
                                if (isLoading) View.GONE else View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        val appBarConfiguration = AppBarConfiguration(findNavController().graph)
        NavigationUI.setupWithNavController(
            binding.toolbar,
            findNavController(),
            appBarConfiguration
        )
        binding.apply {
            if (!destinationName.isNullOrEmpty()) {
                toolbar.title = destinationName
            } else {
                toolbar.title = ""
            }
        }
    }

    private fun setupViewPager() {
        val viewPagerAdapter = DetailsViewPageAdapter(this, destinationId)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(
            binding.tabs,
            binding.viewPager
        ) { tab, position ->
            tab.text = when (position) {
                DESTINATION_DETAILS_ATTRACTION_INDEX -> getString(R.string.attractions_label)
                DESTINATION_DETAILS_REVIEW_INDEX -> getString(R.string.reviews_label)
                else -> getString(R.string.tab_unknown)
            }
        }.attach()
    }
}
