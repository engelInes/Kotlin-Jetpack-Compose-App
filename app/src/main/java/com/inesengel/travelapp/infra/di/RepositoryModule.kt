package com.inesengel.travelapp.infra.di

import com.inesengel.travelapp.core.repository.GeocodingRepository
import com.inesengel.travelapp.core.repository.SettingsRepository
import com.inesengel.travelapp.core.repository.UserRepository
import com.inesengel.travelapp.infra.repository.GeocodingRepositoryImpl
import com.inesengel.travelapp.infra.repository.SettingsRepositoryImpl
import com.inesengel.travelapp.infra.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import project.repository.TravelDestinationRepository
import project.repository.TravelDestinationRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindTravelDestinationRepository(
        impl: TravelDestinationRepositoryImpl
    ): TravelDestinationRepository

    @Binds
    @Singleton
    abstract fun bindGeocodingRepository(
        impl: GeocodingRepositoryImpl
    ): GeocodingRepository

    @Binds
    @Singleton
    abstract fun settingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository
}
