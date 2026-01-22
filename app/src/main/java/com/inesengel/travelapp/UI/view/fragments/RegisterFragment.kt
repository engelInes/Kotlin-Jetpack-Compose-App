package com.inesengel.travelapp.UI.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.inesengel.travelapp.R
import com.inesengel.travelapp.UI.model.NavigationState
import com.inesengel.travelapp.UI.view.activities.MainActivity
import com.inesengel.travelapp.databinding.RegisterScreenFragmentBinding
import com.inesengel.travelapp.UI.view.utils.navigateTo
import com.inesengel.travelapp.UI.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: RegisterScreenFragmentBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RegisterScreenFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupCollectors()
        setupClickListeners()
        setupTextWatchers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    registerViewModel.formState.collect { formState ->
                        binding.apply {
                            nameTextInputLayout.error = formState.usernameError?.let(::getString)
                            emailTextInputLayout.error = formState.emailError?.let(::getString)
                            passwordTextInputLayout.error =
                                formState.passwordError?.let(::getString)
                            confirmPasswordTextInputLayout.error =
                                formState.confirmPasswordError?.let(::getString)
                            registerButton.isEnabled = formState.isDataValid
                        }
                    }
                }

                launch {
                    registerViewModel.navigationState.collect { state ->
                        if (state == NavigationState.GO_TO_MAIN) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.registration_successful_message),
                                Toast.LENGTH_SHORT
                            ).show()

                            goToMainScreen()
                            requireActivity().finish()
                        }
                    }
                }
            }
        }
    }

    private fun setupTextWatchers() {
        binding.apply {
            nameTextInputLayout.editText?.addTextChangedListener { editable ->
                registerViewModel.onUsernameChanged(editable.toString())
            }
            emailTextInputLayout.editText?.addTextChangedListener { editable ->
                registerViewModel.onEmailChanged(editable.toString())
            }
            passwordTextInputLayout.editText?.addTextChangedListener { editable ->
                registerViewModel.onPasswordChanged(editable.toString())
            }
            confirmPasswordTextInputLayout.editText?.addTextChangedListener { editable ->
                registerViewModel.onConfirmPasswordChanged(editable.toString())
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            registerButton.setOnClickListener {
                registerViewModel.onRegister()
            }
            signInButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun goToMainScreen() {
        requireActivity().navigateTo(
            destination = MainActivity::class.java
        )
    }
}