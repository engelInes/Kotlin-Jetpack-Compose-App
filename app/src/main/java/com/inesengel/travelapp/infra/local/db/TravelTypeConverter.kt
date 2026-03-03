package com.inesengel.travelapp.infra.local.db

import androidx.room.TypeConverter
import project.model.TravelType

class TravelTypeConverter {
    @TypeConverter
    fun fromTravelType(type: TravelType): String = type.name

    @TypeConverter
    fun toTravelType(value: String): TravelType = TravelType.valueOf(value)
}
