package com.inesengel.travelapp.core.usecase

import com.inesengel.travelapp.core.repository.SettingsRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class SetDarkModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(isEnabled: Boolean): Flow<Unit> = settingsRepository.setDarkMode(isEnabled)
}