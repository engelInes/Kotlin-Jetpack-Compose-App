package com.inesengel.travelapp.core.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import project.model.TravelDestination
import project.model.TravelType
import project.repository.TravelDestinationRepository
import javax.inject.Inject

class GetDestinationsUseCase @Inject constructor(
    private val travelRepository: TravelDestinationRepository
) {
    operator fun invoke(
        searchQuery: String,
        selectedType: TravelType?
    ): Flow<List<TravelDestination>> {
        return travelRepository.getDestinations()
            .map { destinationWithDetails ->
                destinationWithDetails
                    .map { it.destination }
                    .filter { destination ->
                        matchesSearch(destination, searchQuery) &&
                                matchesType(destination, selectedType)
                    }
            }
    }

    private fun matchesSearch(
        destination: TravelDestination,
        query: String
    ): Boolean {
        if (query.isBlank()) return true

        return destination.name.contains(query, true) ||
                destination.country.contains(query, true)
    }

    private fun matchesType(
        destination: TravelDestination,
        type: TravelType?
    ): Boolean {
        return type == null || destination.type == type
    }
}
