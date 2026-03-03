package com.inesengel.travelapp.UI.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inesengel.travelapp.R
import com.inesengel.travelapp.UI.model.NavigationState
import com.inesengel.travelapp.core.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _navigationEvent = MutableSharedFlow<NavigationState>()
    val navigationEvent: SharedFlow<NavigationState> = _navigationEvent

    private val _playAnimationEvent = MutableStateFlow(false)
    val playAnimationEvent: StateFlow<Boolean> = _playAnimationEvent

    fun startAnimation() {
        viewModelScope.launch {
            _playAnimationEvent.emit(true)
        }
    }

    fun checkLoginStatus() {
        viewModelScope.launch {
            userRepository.isUserLoggedIn().collect { loggedIn ->
                _navigationEvent.emit(
                    if (loggedIn) NavigationState.GO_TO_MAIN else NavigationState.GO_TO_AUTH
                )
            }
        }
    }
}
