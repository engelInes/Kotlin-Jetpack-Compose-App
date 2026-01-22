package com.inesengel.travelapp.UI.view.fragments

import com.inesengel.travelapp.R
import android.app.AlertDialog
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.inesengel.travelapp.UI.binding.adapters.AttractionAdapter
import com.inesengel.travelapp.UI.binding.adapters.ReviewAdapter
import com.inesengel.travelapp.UI.model.NavigationState
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.ARG_DESTINATION_ID
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.INVALID_DESTINATION_ID
import com.inesengel.travelapp.UI.view.utils.Constants.Validation.DATE_FORMAT
import com.inesengel.travelapp.UI.view.utils.Constants.Validation.IMAGE_PICKER_FORMAT
import com.inesengel.travelapp.UI.viewmodel.AddDestinationViewModel
import com.inesengel.travelapp.core.model.ResultState
import com.inesengel.travelapp.core.model.UserReview
import com.inesengel.travelapp.databinding.AddDestinationFragmentBinding
import com.inesengel.travelapp.databinding.AddReviewDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import project.model.TravelType
import java.util.Date
import java.util.Locale
import androidx.core.net.toUri
import com.google.android.material.textfield.TextInputEditText
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.ADD_FORM_PROGRESS
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.ADD_DESTINATION_ATTRACTION_INDEX
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.DESTINATION_DETAILS_INDEX
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.NULL_INDEX
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.ADD_DESTINATION_REVIEWS_INDEX
import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.DEFAULT_STEP_INDEX
import com.inesengel.travelapp.UI.view.utils.copyToInternalStorage
import com.inesengel.travelapp.core.model.AddDestinationFormState
import com.inesengel.travelapp.core.model.DestinationAttraction
import com.inesengel.travelapp.databinding.DialogDeleteConfirmationBinding

@AndroidEntryPoint
class AddDestinationFragment : Fragment() {
    private var _binding: AddDestinationFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddDestinationViewModel by viewModels()
    private var currentStep = DESTINATION_DETAILS_INDEX

    private lateinit var attractionsAdapter: AttractionAdapter
    private lateinit var reviewsAdapter: ReviewAdapter

    private var activeAttractionDialog: AddAttractionDialog? = null
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { safeUri ->
                val localPath = copyToInternalStorage(
                    requireContext(),
                    safeUri,
                    getString(R.string.image_file_name, System.currentTimeMillis())
                )
                activeAttractionDialog?.updateImage(localPath.toUri())
            }
        }
    private val destinationId: Int by lazy {
        arguments?.getInt(
            ARG_DESTINATION_ID,
            INVALID_DESTINATION_ID
        ) ?: INVALID_DESTINATION_ID
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddDestinationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupTypeDropdown()
        setupListeners()
        setupNavigation()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (currentStep > DESTINATION_DETAILS_INDEX) {
                currentStep--
                updateStepUI()
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }
        if (destinationId != INVALID_DESTINATION_ID) {
            binding.apply {
                saveButton.text = getString(R.string.update_button)
                topAppBar.title = getString(R.string.update_destination_desc)
            }
            viewModel.loadDestinationForEdit(destinationId)
        }
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerViews() {
        attractionsAdapter = AttractionAdapter { attraction ->
            showDeleteAttractionDialog(attraction)
        }
        reviewsAdapter = ReviewAdapter()

        binding.apply {
            attractionsRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = attractionsAdapter
            }

            reviewsRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = reviewsAdapter
            }
        }
    }

    private fun setupTypeDropdown() {
        val types = TravelType.entries.map { it.name }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            types
        )

        binding.typeAutocomplete.setAdapter(adapter)
    }

    private fun setupListeners() {
        binding.apply {
            nameLabelEditText.doAfterTextChanged { viewModel.onNameChanged(it.toString()) }
            countryLabelEditText.doAfterTextChanged { viewModel.onCountryChanged(it.toString()) }
            priceLabelEditText.doAfterTextChanged { viewModel.onPriceChanged(it.toString()) }
            durationLabelEditText.doAfterTextChanged { viewModel.onDurationChanged(it.toString()) }

            typeAutocomplete.setOnItemClickListener { _, _, position, _ ->
                val selectedType = TravelType.entries[position]
                viewModel.onTypeChanged(selectedType)
            }

            addAttractionButton.setOnClickListener { showAddAttractionDialog() }
            addReviewButton.setOnClickListener { showAddReviewDialog() }

            saveButton.setOnClickListener {
                viewModel.saveDestination()
            }
        }
    }

    private fun setupNavigation() {
        binding.apply {
            nextButton.setOnClickListener {
                if (currentStep < ADD_DESTINATION_REVIEWS_INDEX) {
                    currentStep++
                    updateStepUI()
                }
            }

            prevButton.setOnClickListener {
                if (currentStep > DESTINATION_DETAILS_INDEX) {
                    currentStep--
                    updateStepUI()
                }
            }

            topAppBar.setNavigationOnClickListener {
                if (currentStep > DESTINATION_DETAILS_INDEX) {
                    currentStep--
                    updateStepUI()
                } else {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun updateStepUI() {
        binding.apply {
            viewFlipper.displayedChild = currentStep
            stepProgress.setProgress(
                (currentStep + DEFAULT_STEP_INDEX) * ADD_FORM_PROGRESS,
                true
            )

            prevButton.visibility =
                if (currentStep == DESTINATION_DETAILS_INDEX) View.GONE else View.VISIBLE
            nextButton.visibility =
                if (currentStep == ADD_DESTINATION_REVIEWS_INDEX) View.GONE else View.VISIBLE
            saveButton.visibility =
                if (currentStep == ADD_DESTINATION_REVIEWS_INDEX) View.VISIBLE else View.GONE

            topAppBar.title = when (currentStep) {
                DESTINATION_DETAILS_INDEX -> {
                    if (destinationId != INVALID_DESTINATION_ID) {
                        getString(R.string.update_destination_desc)
                    } else {
                        getString(R.string.add_destination_desc)
                    }
                }

                ADD_DESTINATION_ATTRACTION_INDEX -> getString(R.string.add_attraction_desc)
                else -> getString(R.string.add_review_desc)
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeFormState() }
                launch { observeAddResult() }
                launch { observeNavigationState() }
            }
        }
    }

    private fun showDeleteAttractionDialog(attraction: DestinationAttraction) {
        val dialogBinding = DialogDeleteConfirmationBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.apply {
            cancelButton.setOnClickListener {
                dialog.dismiss()
            }

            confirmButton.setOnClickListener {
                viewModel.removeAttraction(attraction)
                dialog.dismiss()
            }
        }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private suspend fun observeFormState() {
        viewModel.formState.collectLatest { state ->
            updateFormButtons(state)
            updateFormFields(state)

            attractionsAdapter.submitList(state.attractions)
            reviewsAdapter.submitList(state.reviews)
        }
    }

    private fun updateFormButtons(state: AddDestinationFormState) {
        binding.apply {
            nextButton.isEnabled =
                if (currentStep == DESTINATION_DETAILS_INDEX) state.isDataValid else true
            saveButton.isEnabled = state.isDataValid
        }
    }

    private fun updateFormFields(state: AddDestinationFormState) {
        binding.apply {
            setTextIfChanged(nameLabelEditText, state.name)
            setTextIfChanged(countryLabelEditText, state.country)
            setTextIfChanged(priceLabelEditText, state.price)
            setTextIfChanged(durationLabelEditText, state.duration)

            if (typeAutocomplete.text.toString() != state.type.name) {
                typeAutocomplete.setText(state.type.name, false)
            }
        }
    }

    private fun setTextIfChanged(
        editText: TextInputEditText,
        newValue: String
    ) {
        if (editText.text.toString() != newValue) {
            editText.setText(newValue)
        }
    }

    private suspend fun observeAddResult() {
        viewModel.addResult.collectLatest { result ->
            when (result) {
                is ResultState.Success -> {
                    showToast(R.string.saved_destination_message)
                }

                is ResultState.Error -> {
                    showToast(result.messageResId, Toast.LENGTH_LONG)
                }
            }
        }
    }

    private fun showToast(
        messageRes: Int,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        Toast.makeText(requireContext(), getString(messageRes), duration).show()
    }

    private suspend fun observeNavigationState() {
        viewModel.navigationState.collectLatest { navState ->
            when (navState) {
                NavigationState.GO_TO_MAIN ->
                    findNavController().popBackStack()

                NavigationState.GO_TO_MAIN_AND_FINISH ->
                    requireActivity().finish()

                NavigationState.GO_TO_AUTH -> Unit
            }
        }
    }

    private fun showAddAttractionDialog() {
        activeAttractionDialog = AddAttractionDialog(
            requireContext(),
            onPickImageClicked = {
                pickImageLauncher.launch(IMAGE_PICKER_FORMAT)
            },
            onConfirm = { attraction ->
                viewModel.addAttractionObject(attraction)
            }
        )
        activeAttractionDialog?.displayDialog()
    }

    private fun showAddReviewDialog() {
        val dialogBinding = AddReviewDialogBinding.inflate(layoutInflater)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.add_review_desc))
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.add_button_name)) { _, _ ->
                val username = viewModel.currentUserName.value
                val comment = dialogBinding.reviewCommentInput.text.toString()
                val ratingStr = dialogBinding.reviewRatingInput.text.toString()

                if (username.isBlank() || ratingStr.isBlank()) {
                    showToast(R.string.username_error)
                    return@setPositiveButton
                }

                val rating = ratingStr.toFloatOrNull()?.coerceIn(0f, 5f) ?: 0f
                val currentDate: DateFormat = SimpleDateFormat(
                    DATE_FORMAT,
                    Locale.getDefault()
                )

                viewModel.addReview(
                    UserReview(
                        id = NULL_INDEX,
                        destinationId = NULL_INDEX,
                        reviewerName = username,
                        rating = rating,
                        reviewText = comment,
                        date = currentDate.format(Date())
                    )
                )
            }
            .setNegativeButton(
                getString(R.string.cancel_button_name),
                null
            )
            .show()
    }
}