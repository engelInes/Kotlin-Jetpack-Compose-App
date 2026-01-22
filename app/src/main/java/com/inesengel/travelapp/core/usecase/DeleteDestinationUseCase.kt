package com.inesengel.travelapp.core.usecase

import project.repository.TravelDestinationRepository
import javax.inject.Inject

class DeleteDestinationUseCase @Inject constructor(
    private val repository: TravelDestinationRepository
) {
    suspend operator fun invoke(id: Int): Boolean {
        return repository.deleteDestination(id)
    }
}