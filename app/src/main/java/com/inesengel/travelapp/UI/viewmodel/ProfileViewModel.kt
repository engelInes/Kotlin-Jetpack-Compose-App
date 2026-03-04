package com.inesengel.travelapp.UI.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inesengel.travelapp.core.usecase.GetUsernameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUsernameUseCase: GetUsernameUseCase
) : ViewModel() {

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            getUsernameUseCase().collect { name ->
                _userName.value = name
            }
        }
    }
}
