package com.inesengel.travelapp.core.usecase

import com.inesengel.travelapp.core.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsernameUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<String> {
        return userRepository.getUserName()
    }
}