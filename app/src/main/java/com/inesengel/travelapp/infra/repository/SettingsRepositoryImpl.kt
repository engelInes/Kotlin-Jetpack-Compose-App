package com.inesengel.travelapp.infra.repository

import com.inesengel.travelapp.core.repository.SettingsRepository
import com.inesengel.travelapp.infra.datasource.SharedPreferencesHelper
import com.inesengel.travelapp.infra.utils.Constants.Database.KEY_DARK_MODE
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SettingsRepositoryImpl @Inject constructor(
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : SettingsRepository {

    override fun isDarkModeEnabled(): Flow<Boolean> = flow {
        emit(
            sharedPreferencesHelper.getBoolean(
                KEY_DARK_MODE,
                false
            )
        )
    }

    override fun setDarkMode(enabled: Boolean): Flow<Unit> = flow {
        sharedPreferencesHelper.saveBoolean(
            KEY_DARK_MODE,
            enabled
        )
        emit(Unit)
    }
}