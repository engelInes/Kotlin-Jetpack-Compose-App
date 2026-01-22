package com.inesengel.travelapp.core.usecase

import com.inesengel.travelapp.core.model.TravelDestinationWithDetails
import kotlinx.coroutines.flow.Flow
import project.repository.TravelDestinationRepository
import javax.inject.Inject

class GetFavoriteDestinationsUseCase @Inject constructor(private val travelDestinationRepository: TravelDestinationRepository) {
    operator fun invoke(): Flow<List<TravelDestinationWithDetails>> {
        return travelDestinationRepository.getFavoriteDestinations()
    }
}