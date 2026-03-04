package com.inesengel.travelapp.core.repository

import com.inesengel.travelapp.core.model.ResultState
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun isUserLoggedIn(): Flow<Boolean>
    fun registerUser(
        username: String,
        email: String,
        password: String
    ): Flow<ResultState>

    fun loginUser(
        email: String,
        password: String,
        keepLoggedIn: Boolean
    ): Flow<ResultState>

    fun logoutUser(): Flow<Boolean>

    fun getUserName(): Flow<String>

    fun getEmail(): Flow<String>

    fun updatePersonalData(
        username: String,
        email: String
    ): Flow<ResultState>
}
