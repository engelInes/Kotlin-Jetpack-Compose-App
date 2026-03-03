package com.inesengel.travelapp.infra.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.inesengel.travelapp.core.model.TravelDestinationWithDetails
import kotlinx.coroutines.flow.Flow
import project.model.TravelDestination

@Dao
interface TravelDestinationDao {
    @Insert
    suspend fun insertDestination(destination: TravelDestination): Long

    @Transaction
    @Query("SELECT * FROM travel_destinations")
    fun getDestinationsWithDetails(): Flow<List<TravelDestinationWithDetails>>

    @Transaction
    @Query("SELECT * FROM travel_destinations WHERE id = :id")
    fun getDestinationById(id: Int): Flow<TravelDestinationWithDetails?>

    @Query("DELETE FROM travel_destinations WHERE id = :id")
    suspend fun deleteDestinationById(id: Int): Int

    @Update
    suspend fun updateDestination(destination: TravelDestination)

    @Transaction
    @Query("SELECT * FROM travel_destinations WHERE isFavorite = 1")
    fun getFavoriteDestinations(): Flow<List<TravelDestinationWithDetails>>

    @Query("UPDATE travel_destinations SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)
}
