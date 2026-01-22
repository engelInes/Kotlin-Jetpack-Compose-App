package com.inesengel.travelapp.core.usecase

import com.inesengel.travelapp.core.repository.SettingsRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetDarkModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<Boolean> = settingsRepository.isDarkModeEnabled()
}