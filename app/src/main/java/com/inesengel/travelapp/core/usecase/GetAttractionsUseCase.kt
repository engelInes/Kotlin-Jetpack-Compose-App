package com.inesengel.travelapp.core.usecase

import com.inesengel.travelapp.core.model.DestinationAttraction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import project.repository.TravelDestinationRepository
import javax.inject.Inject

class GetAttractionsUseCase @Inject constructor(
    private val repository: TravelDestinationRepository
) {
    operator fun invoke(destinationId: Int): Flow<List<DestinationAttraction>> {
        val destination = repository.getDestinationById(destinationId)
            .map { destination ->
                destination?.attractions ?: emptyList()
            }
        return destination
    }
}
