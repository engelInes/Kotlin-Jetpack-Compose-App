package com.inesengel.travelapp.infra.repository

import android.location.Geocoder
import com.inesengel.travelapp.core.model.MapLocation
import com.inesengel.travelapp.core.repository.GeocodingRepository
import com.inesengel.travelapp.infra.utils.Constants.Database.GEOCODER_MAX_RESULTS
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class GeocodingRepositoryImpl @Inject constructor(
    private val geocoder: Geocoder
) : GeocodingRepository {
    Log.d("pr-label test", "GeocodingRepositoryImpl created")
    override suspend fun getCoordinatesFromAddress(address: String): MapLocation? {
        return try {
            val addresses = geocoder.getFromLocationName(
                address,
                GEOCODER_MAX_RESULTS
            )
            addresses?.firstOrNull()?.let {
                MapLocation(
                    it.latitude,
                    it.longitude,
                    address
                )
            }
        } catch (e: Exception) {
            null
        }
    }
}
