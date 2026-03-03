package com.inesengel.travelapp.infra.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.inesengel.travelapp.core.model.DestinationAttraction
import com.inesengel.travelapp.core.model.UserReview
import com.inesengel.travelapp.infra.local.dao.DestinationAttractionDao
import com.inesengel.travelapp.infra.local.dao.TravelDestinationDao
import com.inesengel.travelapp.infra.local.dao.UserReviewDao
import com.inesengel.travelapp.infra.utils.Constants.Database.DATABASE_VERSION
import project.model.TravelDestination

@Database(
    entities = [
        TravelDestination::class,
        DestinationAttraction::class,
        UserReview::class
    ],
    version = DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(TravelTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun travelDestinationDao(): TravelDestinationDao
    abstract fun attractionDao(): DestinationAttractionDao
    abstract fun reviewDao(): UserReviewDao
}
