package com.inesengel.travelapp.core.usecase

import com.inesengel.travelapp.core.model.ResultState
import com.inesengel.travelapp.core.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdatePersonalDataUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(
        username: String,
        email: String
    ): Flow<ResultState> {
        return userRepository.updatePersonalData(username, email)
    }
}