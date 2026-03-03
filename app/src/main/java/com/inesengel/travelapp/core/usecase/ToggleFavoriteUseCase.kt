package com.inesengel.travelapp.core.usecase

import project.repository.TravelDestinationRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val travelDestinationRepository: TravelDestinationRepository
) {
    suspend operator fun invoke(
        id: Int,
        isFavorite: Boolean
    ) {
        return travelDestinationRepository.toggleFavorite(
            id,
            isFavorite
        )
    }
}
