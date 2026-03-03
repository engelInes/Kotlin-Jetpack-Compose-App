package com.inesengel.travelapp.UI.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inesengel.travelapp.R
import com.inesengel.travelapp.UI.model.NavigationState
import com.inesengel.travelapp.UI.view.utils.Constants.Validation.ONE_DIGIT_REGEX
import com.inesengel.travelapp.UI.view.utils.Constants.Validation.ONE_LOWERCASE_CHARACTER_REGEX
import com.inesengel.travelapp.UI.view.utils.Constants.Validation.ONE_SPECIAL_CHARACTER_REGEX
import com.inesengel.travelapp.UI.view.utils.Constants.Validation.ONE_UPPERCASE_CHARACTER_REGEX
import com.inesengel.travelapp.UI.view.utils.Constants.Validation.PASSWORD_LENGTH
import com.inesengel.travelapp.core.model.RegisterFormState
import com.inesengel.travelapp.core.usecase.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {
    private val _usernameInput = MutableStateFlow("")
    private val _emailInput = MutableStateFlow("")
    private val _passwordInput = MutableStateFlow("")
    private val _confirmPasswordInput = MutableStateFlow("")

    private val _navigationState = MutableSharedFlow<NavigationState>()
    val navigationState: SharedFlow<NavigationState> = _navigationState

    val formState: StateFlow<RegisterFormState> = combine(
        _usernameInput,
        _emailInput,
        _passwordInput,
        _confirmPasswordInput
    ) { username, email, password, confirm ->
        val usernameError = getUsernameError(username)
        val emailError = getEmailError(email)
        val passwordError = getPasswordError(password)
        val confirmPasswordError = getConfirmPasswordError(password, confirm)

        val displayUsernameError = if (username.isNotEmpty()) usernameError else null
        val displayEmailError = if (email.isNotEmpty()) emailError else null
        val displayPasswordError = if (password.isNotEmpty()) passwordError else null
        val displayConfirmError = if (confirm.isNotEmpty()) confirmPasswordError else null

        RegisterFormState(
            usernameError = displayUsernameError,
            emailError = displayEmailError,
            passwordError = displayPasswordError,
            confirmPasswordError = displayConfirmError,
            isDataValid = usernameError == null &&
                    emailError == null &&
                    passwordError == null &&
                    confirmPasswordError == null
        )
    }.stateIn(
        viewModelScope + Dispatchers.IO,
        SharingStarted.Lazily,
        RegisterFormState()
    )

    fun onRegister() {
        if (!formState.value.isDataValid) {
            return
        }
        val username = _usernameInput.value
        val email = _emailInput.value
        val password = _passwordInput.value

        viewModelScope.launch {
            registerUserUseCase(
                username,
                email,
                password
            ).collect {
                _navigationState.emit(NavigationState.GO_TO_MAIN)
            }
        }
    }

    fun onUsernameChanged(username: String) {
        _usernameInput.value = username
    }

    fun onEmailChanged(email: String) {
        _emailInput.value = email
    }

    fun onPasswordChanged(password: String) {
        _passwordInput.value = password
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _confirmPasswordInput.value = confirmPassword
    }

    private fun getEmailError(email: String): Int? {
        return when {
            email.isEmpty() -> R.string.email_required_error
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.invalid_email_error
            else -> null
        }
    }

    private fun getUsernameError(username: String): Int? {
        return when {
            username.isEmpty() -> R.string.username_error
            else -> null
        }
    }

    private fun getPasswordError(password: String): Int? {
        return when {
            password.length < PASSWORD_LENGTH -> R.string.password_length
            !ONE_UPPERCASE_CHARACTER_REGEX.toRegex()
                .containsMatchIn(password) -> R.string.password_uppercase_constraint

            !ONE_LOWERCASE_CHARACTER_REGEX.toRegex()
                .containsMatchIn(password) -> R.string.password_lowercase_constraint

            !ONE_DIGIT_REGEX.toRegex()
                .containsMatchIn(password) -> R.string.password_one_digit_constraint

            !ONE_SPECIAL_CHARACTER_REGEX.toRegex()
                .containsMatchIn(password) -> R.string.special_characters_error

            else -> null
        }
    }

    private fun getConfirmPasswordError(
        password: String,
        confirm: String
    ): Int? {
        return when {
            confirm.isNotEmpty() && password != confirm -> R.string.password_mismatch_error
            else -> null
        }
    }
}
