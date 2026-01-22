package com.inesengel.travelapp.infra.di

import android.app.Application
import androidx.room.Room
import com.inesengel.travelapp.infra.local.dao.DestinationAttractionDao
import com.inesengel.travelapp.infra.local.dao.TravelDestinationDao
import com.inesengel.travelapp.infra.local.dao.UserReviewDao
import com.inesengel.travelapp.infra.local.db.AppDatabase
import com.inesengel.travelapp.infra.utils.Constants.Database.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .build()
    }

    @Provides
    fun provideTravelDestinationDao(db: AppDatabase): TravelDestinationDao {
        return db.travelDestinationDao()
    }

    @Provides
    fun provideAttractionDao(db: AppDatabase): DestinationAttractionDao {
        return db.attractionDao()
    }

    @Provides
    fun provideReviewsDao(db: AppDatabase): UserReviewDao {
        return db.reviewDao()
    }
}
