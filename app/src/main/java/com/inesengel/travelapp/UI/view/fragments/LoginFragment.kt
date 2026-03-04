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
import com.inesengel.travelapp.databinding.LoginScreenFragmentBinding
import com.inesengel.travelapp.UI.view.utils.navigateTo
import com.inesengel.travelapp.UI.viewmodel.LoginViewModel
import com.inesengel.travelapp.core.model.ResultState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: LoginScreenFragmentBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LoginScreenFragmentBinding.inflate(inflater, container, false)
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
                    loginViewModel.loginFormState.collect { state ->
                        binding.apply {
                            loginButton.isEnabled = true
                            emailTextInputLayout.error = state.emailError?.let { getString(it) }
                            passwordTextInputLayout.error =
                                state.passwordError?.let { getString(it) }
                        }
                    }
                }

                launch {
                    loginViewModel.loginResult.collect { result ->
                        val message = when (result) {
                            is ResultState.Success -> getString(
                                R.string.welcome_back,
                                result.username
                            )

                            is ResultState.Error -> getString(result.messageResId)
                        }
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }

                launch {
                    loginViewModel.navigationState.collect { navigationState ->
                        when (navigationState) {
                            NavigationState.GO_TO_MAIN_AND_FINISH -> {
                                goToMainScreen()
                                requireActivity().finish()
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private fun setupTextWatchers() {
        binding.apply {
            emailEditText.addTextChangedListener { editable ->
                loginViewModel.onEmailChanged(editable.toString())
            }

            passwordEditText.addTextChangedListener { editable ->
                loginViewModel.onPasswordChanged(editable.toString())
            }

            keepLoggedInCheckbox.setOnCheckedChangeListener { _, isChecked ->
                loginViewModel.onKeepLoggedInChanged(isChecked)
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            loginButton.setOnClickListener {
                loginViewModel.onLogin()
            }
            signUpButton.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_from_login_to_register_fragment)
            }
        }
    }

    private fun goToMainScreen() {
        requireActivity().navigateTo(
            destination = MainActivity::class.java
        )
    }
}
