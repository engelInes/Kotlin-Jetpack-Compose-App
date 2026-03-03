package com.inesengel.travelapp.infra.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.inesengel.travelapp.core.model.DestinationAttraction
import kotlinx.coroutines.flow.Flow

@Dao
interface DestinationAttractionDao {
    @Insert
    suspend fun insertAttractions(attractions: List<DestinationAttraction>)

    @Transaction
    @Query("SELECT * FROM attractions WHERE destinationId = :destinationId")
    fun getAttractionsForDestination(destinationId: Int): Flow<List<DestinationAttraction>>

    @Query("DELETE FROM attractions WHERE destinationId = :destinationId")
    suspend fun deleteAttractionsByDestinationId(destinationId: Int)
}
