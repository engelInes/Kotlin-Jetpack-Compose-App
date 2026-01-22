package com.inesengel.travelapp.infra.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.inesengel.travelapp.core.model.UserReview
import kotlinx.coroutines.flow.Flow

@Dao
interface UserReviewDao {
    @Insert
    suspend fun insertReviews(reviews: List<UserReview>)

    @Transaction
    @Query("SELECT * FROM reviews WHERE destinationId = :destinationId")
    fun getReviewsForDestination(destinationId: Int): Flow<List<UserReview>>

    @Query("DELETE FROM reviews WHERE destinationId = :destinationId")
    suspend fun deleteReviewsByDestinationId(destinationId: Int)
}