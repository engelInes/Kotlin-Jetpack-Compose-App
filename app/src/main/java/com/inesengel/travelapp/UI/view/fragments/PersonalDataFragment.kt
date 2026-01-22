package com.inesengel.travelapp.UI.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.inesengel.travelapp.R
import com.inesengel.travelapp.UI.viewmodel.PersonalDataViewModel
import com.inesengel.travelapp.databinding.PersonalDataFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonalDataFragment : Fragment() {
    private var _binding: PersonalDataFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PersonalDataViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PersonalDataFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTextWatchers()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupTextWatchers() {
        binding.apply {
            usernameInput.doAfterTextChanged { text ->
                viewModel.onUsernameChanged(text.toString())
            }

            emailInput.doAfterTextChanged { text ->
                viewModel.onEmailChanged(text.toString())
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            backButton.setOnClickListener {
                findNavController().navigateUp()
            }

            saveButton.setOnClickListener {
                viewModel.onSave()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.initialData.collect { data ->
                        binding.apply {
                            if (usernameInput.text.isNullOrEmpty()) {
                                usernameInput.setText(data.username)
                                emailInput.setText(data.email)
                            }
                        }
                    }
                }
                launch {
                    viewModel.formState.collect { formState ->
                        binding.apply {
                            usernameInputLayout.error = formState.usernameError?.let {
                                getString(it)
                            }
                            emailInputLayout.error = formState.emailError?.let {
                                getString(it)
                            }

                            saveButton.isEnabled = formState.isDataValid
                        }
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.apply {
                            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                            saveButton.isEnabled =
                                !isLoading && viewModel.formState.value.isDataValid
                        }
                    }
                }

                launch {
                    viewModel.saveSuccess.collect { success ->
                        if (success) {
                            Toast.makeText(
                                requireContext(),
                                R.string.profile_updated_successfully,
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}