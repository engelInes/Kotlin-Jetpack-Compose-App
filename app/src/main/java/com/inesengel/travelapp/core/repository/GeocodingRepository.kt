package com.inesengel.travelapp.core.repository

import com.inesengel.travelapp.core.model.MapLocation

interface GeocodingRepository {
    suspend fun getCoordinatesFromAddress(address: String): MapLocation?
}
