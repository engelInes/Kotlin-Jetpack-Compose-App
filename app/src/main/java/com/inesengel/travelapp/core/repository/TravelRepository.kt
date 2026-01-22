package project.repository

import com.inesengel.travelapp.core.model.DestinationAttraction
import com.inesengel.travelapp.core.model.TravelDestinationWithDetails
import com.inesengel.travelapp.core.model.UserReview
import kotlinx.coroutines.flow.Flow
import project.model.TravelDestination

interface TravelDestinationRepository {
    fun getDestinations(): Flow<List<TravelDestinationWithDetails>>
    fun getDestinationById(id: Int): Flow<TravelDestinationWithDetails?>
    suspend fun deleteDestination(id: Int): Boolean
    suspend fun addDestination(
        destination: TravelDestination,
        attractions: List<DestinationAttraction>,
        reviews: List<UserReview>
    ): Long

    fun getFavoriteDestinations(): Flow<List<TravelDestinationWithDetails>>
    suspend fun toggleFavorite(id: Int, isFavorite: Boolean)
    suspend fun updateDestination(destination: TravelDestination)
}