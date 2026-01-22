package com.inesengel.travelapp.infra.datasource

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class SharedPreferencesHelper @Inject constructor(
    private val prefs: SharedPreferences
) {
    fun saveString(
        key: String,
        value: String
    ) {
        prefs.edit { putString(key, value) }
    }

    fun getString(
        key: String,
        default: String = ""
    ): String {
        return prefs.getString(key, default) ?: default
    }

    fun saveBoolean(
        key: String,
        value: Boolean
    ) {
        prefs.edit { putBoolean(key, value) }
    }

    fun getBoolean(
        key: String,
        default: Boolean = false
    ): Boolean {
        return prefs.getBoolean(key, default)
    }

    fun saveInt(
        key: String,
        value: Int
    ) {
        prefs.edit { putInt(key, value) }
    }

    fun getInt(
        key: String,
        default: Int = 0
    ): Int {
        return prefs.getInt(key, default)
    }

    fun getUsername(): String {
        return getString(KEY_USERNAME, "")
    }

    fun getEmail(): String {
        return getString(KEY_EMAIL, "")
    }

    fun getPassword(): String {
        return getString(KEY_PASSWORD, "")
    }

    fun isLoggedIn(): Boolean {
        return getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        saveBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
    }

    fun clear() {
        prefs.edit { clear() }
    }

    companion object {
        const val KEY_USERNAME = "username"
        const val KEY_EMAIL = "email"
        const val KEY_PASSWORD = "password"
        const val KEY_IS_LOGGED_IN = "isLoggedIn"
    }
}