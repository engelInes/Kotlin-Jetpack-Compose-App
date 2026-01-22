package com.inesengel.travelapp.core.usecase

import com.inesengel.travelapp.core.model.UserReview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import project.repository.TravelDestinationRepository
import javax.inject.Inject

class GetUserReviewsUseCase @Inject constructor(
    private val repository: TravelDestinationRepository
) {
    operator fun invoke(destinationId: Int): Flow<List<UserReview>> {
        val destination = repository.getDestinationById(destinationId)
            .map { destination ->
                destination?.reviews ?: emptyList()
            }
        return destination
    }
}