package com.inesengel.travelapp.infra.repository

import com.inesengel.travelapp.R
import com.inesengel.travelapp.core.model.ResultState
import com.inesengel.travelapp.core.repository.UserRepository
import com.inesengel.travelapp.infra.datasource.SharedPreferencesHelper
import com.inesengel.travelapp.infra.utils.Constants.DataSource.RESULT_STATE_SUCCESS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : UserRepository {
    override fun isUserLoggedIn(): Flow<Boolean> = flow {
        emit(sharedPreferencesHelper.isLoggedIn())
    }

    override fun registerUser(
        username: String,
        email: String, password: String
    ): Flow<ResultState> =
        flow {
            sharedPreferencesHelper.saveString(SharedPreferencesHelper.KEY_USERNAME, username)
            sharedPreferencesHelper.saveString(SharedPreferencesHelper.KEY_EMAIL, email)
            sharedPreferencesHelper.saveString(SharedPreferencesHelper.KEY_PASSWORD, password)
            sharedPreferencesHelper.setLoggedIn(true)
            emit(ResultState.Success(username))
        }

    override fun loginUser(
        email: String,
        password: String,
        keepLoggedIn: Boolean
    ): Flow<ResultState> =
        flow {
            val storedEmail = sharedPreferencesHelper.getEmail()
            val storedPassword = sharedPreferencesHelper.getPassword()
            val storedUsername = sharedPreferencesHelper.getUsername()

            val result = when {
                storedEmail.isEmpty() -> ResultState.Error(R.string.register_first_message)
                storedEmail != email -> ResultState.Error(R.string.invalid_email_error)
                storedPassword != password -> ResultState.Error(R.string.invalid_field)
                else -> {
                    sharedPreferencesHelper.setLoggedIn(keepLoggedIn)
                    ResultState.Success(storedUsername)
                }
            }
            emit(result)
        }

    override fun logoutUser(): Flow<Boolean> = flow {
        sharedPreferencesHelper.setLoggedIn(false)
        emit(true)
    }

    override fun getUserName(): Flow<String> = flow {
        emit(sharedPreferencesHelper.getUsername())
    }

    override fun getEmail(): Flow<String> = flow {
        emit(sharedPreferencesHelper.getEmail())
    }

    override fun updatePersonalData(
        username: String,
        email: String
    ): Flow<ResultState> = flow {
        if (username.isBlank() || email.isBlank()) {
            emit(ResultState.Error(R.string.invalid_field))
        } else {
            sharedPreferencesHelper.saveString(
                SharedPreferencesHelper.KEY_USERNAME,
                username
            )
            sharedPreferencesHelper.saveString(
                SharedPreferencesHelper.KEY_EMAIL,
                email
            )
            emit(ResultState.Success(RESULT_STATE_SUCCESS))
        }
    }
}
