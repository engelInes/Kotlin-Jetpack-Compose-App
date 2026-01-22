package com.inesengel.travelapp.core.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun isDarkModeEnabled(): Flow<Boolean>
    fun setDarkMode(enabled: Boolean): Flow<Unit>
}