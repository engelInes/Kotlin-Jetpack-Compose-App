package com.inesengel.travelapp.core.usecase

import com.inesengel.travelapp.core.model.TravelDestinationWithDetails
import kotlinx.coroutines.flow.Flow
import project.repository.TravelDestinationRepository
import javax.inject.Inject

class GetDestinationByIdUseCase @Inject constructor(
    private val repository: TravelDestinationRepository
) {
    operator fun invoke(destinationId: Int): Flow<TravelDestinationWithDetails?> {
        return repository.getDestinationById(destinationId)
    }
}
