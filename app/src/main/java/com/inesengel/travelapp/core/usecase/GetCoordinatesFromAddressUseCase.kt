package com.inesengel.travelapp.core.usecase

import com.inesengel.travelapp.core.model.MapLocation
import com.inesengel.travelapp.core.repository.GeocodingRepository
import javax.inject.Inject

class GetCoordinatesFromAddressUseCase @Inject constructor(
    private val geocodingRepository: GeocodingRepository
) {
    suspend operator fun invoke(address: String): MapLocation? {
        return geocodingRepository.getCoordinatesFromAddress(address)
    }
}