package com.inesengel.travelapp.core.usecase

import com.inesengel.travelapp.core.model.ResultState
import com.inesengel.travelapp.core.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(
        email: String,
        password: String,
        keepLoggedIn: Boolean
    ): Flow<ResultState> {
        return userRepository.loginUser(
            email,
            password,
            keepLoggedIn
        )
    }
}