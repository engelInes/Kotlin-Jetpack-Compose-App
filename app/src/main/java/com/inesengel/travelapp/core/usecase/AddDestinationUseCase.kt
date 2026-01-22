package com.inesengel.travelapp.core.usecase

import com.inesengel.travelapp.core.model.DestinationAttraction
import com.inesengel.travelapp.core.model.UserReview
import jakarta.inject.Inject
import project.model.TravelDestination
import project.repository.TravelDestinationRepository

class AddDestinationUseCase @Inject constructor(
    private val repository: TravelDestinationRepository
) {
    suspend operator fun invoke(
        destination: TravelDestination,
        attractions: List<DestinationAttraction>,
        reviews: List<UserReview>
    ): Long {
        return repository.addDestination(
            destination,
            attractions,
            reviews
        )
    }
}