package com.inesengel.travelapp.UI.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inesengel.travelapp.R
import com.inesengel.travelapp.UI.view.utils.Constants.Validation.CHARACTERS_REGEX
import com.inesengel.travelapp.core.model.PersonalDataFormState
import com.inesengel.travelapp.core.usecase.GetEmailUseCase
import com.inesengel.travelapp.core.usecase.GetUsernameUseCase
import com.inesengel.travelapp.core.usecase.UpdatePersonalDataUseCase
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
class PersonalDataViewModel @Inject constructor(
    private val getEmailUseCase: GetEmailUseCase,
    private val getUsernameUseCase: GetUsernameUseCase,
    private val updatePersonalDataUseCase: UpdatePersonalDataUseCase
) : ViewModel() {

    private val _usernameInput = MutableStateFlow("")
    private val _emailInput = MutableStateFlow("")

    private val _saveSuccess = MutableSharedFlow<Boolean>()
    val saveSuccess: SharedFlow<Boolean> = _saveSuccess

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    data class InitialData(val username: String, val email: String)

    private val _initialData = MutableSharedFlow<InitialData>(replay = 1)
    val initialData: SharedFlow<InitialData> = _initialData

    val formState: StateFlow<PersonalDataFormState> = combine(
        _usernameInput,
        _emailInput
    ) { username, email ->
        val usernameError = getUsernameError(username)
        val emailError = getEmailError(email)

        PersonalDataFormState(
            usernameError = usernameError,
            emailError = emailError,
            isDataValid = usernameError == null &&
                    emailError == null &&
                    username.isNotEmpty() &&
                    email.isNotEmpty()
        )
    }.stateIn(
        viewModelScope + Dispatchers.IO,
        SharingStarted.Lazily,
        PersonalDataFormState()
    )

    init {
        loadPersonalData()
    }

    fun onUsernameChanged(username: String) {
        _usernameInput.value = username
    }

    fun onEmailChanged(email: String) {
        _emailInput.value = email
    }

    fun onSave() {
        viewModelScope.launch {
            _isLoading.value = true
            updatePersonalDataUseCase(
                _usernameInput.value,
                _emailInput.value
            ).collect {
                _isLoading.value = false
                _saveSuccess.emit(true)
            }
        }
    }

    private fun getUsernameError(username: String): Int? {
        return when {
            username.isNotEmpty() && username.length < 2 -> R.string.username_error
            username.isNotEmpty() && !username.matches(Regex(CHARACTERS_REGEX)) -> R.string.invalid_email_error
            else -> null
        }
    }

    private fun getEmailError(email: String): Int? {
        return when {
            email.isEmpty() -> null
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.invalid_email_error
            else -> null
        }
    }

    private fun loadPersonalData() {
        viewModelScope.launch {
            _isLoading.value = true
            combine(
                getUsernameUseCase(),
                getEmailUseCase()
            ) { user, email ->
                InitialData(user, email)
            }.collect { data ->
                _usernameInput.value = data.username
                _emailInput.value = data.email
                _initialData.emit(data)
                _isLoading.value = false
            }
        }
    }
}
