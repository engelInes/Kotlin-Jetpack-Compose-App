package com.inesengel.travelapp.UI.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inesengel.travelapp.core.usecase.GetDarkModeUseCase
import com.inesengel.travelapp.core.usecase.SetDarkModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getDarkModeUseCase: GetDarkModeUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase
) : ViewModel() {
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    init {
        loadThemeSettings()
    }

    private fun loadThemeSettings() {
        viewModelScope.launch {
            getDarkModeUseCase().collect { isEnabled ->
                _isDarkMode.value = isEnabled
                applyTheme(isEnabled)
            }
        }
    }

    fun onThemeChanged(isEnabled: Boolean) {
        if (_isDarkMode.value == isEnabled) return

        viewModelScope.launch {
            setDarkModeUseCase(isEnabled).collect {
                _isDarkMode.value = isEnabled
                applyTheme(isEnabled)
            }
        }
    }

    private fun applyTheme(isEnabled: Boolean) {
        val mode = if (isEnabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}