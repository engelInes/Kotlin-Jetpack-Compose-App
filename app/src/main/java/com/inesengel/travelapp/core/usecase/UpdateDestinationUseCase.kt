package com.inesengel.travelapp.core.usecase

import javax.inject.Inject
import project.model.TravelDestination
import project.repository.TravelDestinationRepository

class UpdateDestinationUseCase @Inject constructor(
    private val travelDestinationRepository: TravelDestinationRepository
) {
    suspend operator fun invoke(destination: TravelDestination) {
        travelDestinationRepository.updateDestination(destination)
    }
}
