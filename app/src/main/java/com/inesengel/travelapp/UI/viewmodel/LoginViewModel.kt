package com.inesengel.travelapp.UI.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inesengel.travelapp.R
import com.inesengel.travelapp.UI.model.NavigationState
import com.inesengel.travelapp.core.model.LoginFormState
import com.inesengel.travelapp.core.model.ResultState
import com.inesengel.travelapp.core.usecase.LoginUserUseCase
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
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _keepLoggedIn = MutableStateFlow(false)

    private val _loginResult = MutableSharedFlow<ResultState>()
    val loginResult: SharedFlow<ResultState> = _loginResult

    private val _showErrors = MutableStateFlow(false)

    private val _navigationState = MutableSharedFlow<NavigationState>()
    val navigationState: SharedFlow<NavigationState> = _navigationState

    var loginFormState: StateFlow<LoginFormState> = combine(
        _email,
        _password,
        _keepLoggedIn,
        _showErrors
    ) { email, password, keepLoggedIn, showErrors ->
        val emailError = if (showErrors && email.isBlank()) R.string.email_required_error else null
        val passwordError =
            if (showErrors && password.isBlank()) R.string.password_required_error else null

        LoginFormState(
            email = email,
            password = password,
            keepLoggedIn = keepLoggedIn,
            emailError = emailError,
            passwordError = passwordError,
            isDataValid = email.isNotBlank() && password.isNotBlank()
        )
    }.stateIn(
        scope = viewModelScope + Dispatchers.IO,
        started = SharingStarted.Lazily,
        initialValue = LoginFormState()
    )

    fun onLogin() {
        _showErrors.value = true
        val form = loginFormState.value
        if (!form.isDataValid) return

        viewModelScope.launch {
            loginUserUseCase(
                form.email,
                form.password,
                form.keepLoggedIn
            ).collect { result ->
                _loginResult.emit(result)

                if (result is ResultState.Success) {
                    _navigationState.emit(NavigationState.GO_TO_MAIN_AND_FINISH)
                }
            }
        }
    }

    fun onEmailChanged(email: String) {
        _email.value = email
    }

    fun onPasswordChanged(password: String) {
        _password.value = password
    }

    fun onKeepLoggedInChanged(keepLoggedIn: Boolean) {
        _keepLoggedIn.value = keepLoggedIn
    }
}
