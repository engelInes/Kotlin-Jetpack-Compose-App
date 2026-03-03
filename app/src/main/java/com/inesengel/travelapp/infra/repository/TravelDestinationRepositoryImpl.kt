package project.repository

import com.inesengel.travelapp.UI.view.utils.Constants.Navigation.NULL_INDEX
import com.inesengel.travelapp.core.model.DestinationAttraction
import com.inesengel.travelapp.core.model.TravelDestinationWithDetails
import com.inesengel.travelapp.core.model.UserReview
import com.inesengel.travelapp.infra.local.dao.DestinationAttractionDao
import com.inesengel.travelapp.infra.local.dao.TravelDestinationDao
import com.inesengel.travelapp.infra.local.dao.UserReviewDao
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import project.model.TravelDestination

@Singleton
class TravelDestinationRepositoryImpl @Inject constructor(
    private val travelDestinationDao: TravelDestinationDao,
    private val attractionDao: DestinationAttractionDao,
    private val reviewDao: UserReviewDao
) : TravelDestinationRepository {
    override fun getDestinations(): Flow<List<TravelDestinationWithDetails>> {
        return travelDestinationDao.getDestinationsWithDetails()
    }

    override fun getDestinationById(id: Int): Flow<TravelDestinationWithDetails?> {
        return travelDestinationDao.getDestinationById(id)
    }

    override suspend fun deleteDestination(id: Int): Boolean {
        return travelDestinationDao.deleteDestinationById(id) > NULL_INDEX
    }

    override suspend fun addDestination(
        destination: TravelDestination,
        attractions: List<DestinationAttraction>,
        reviews: List<UserReview>
    ): Long {
        val destinationId: Long

        if (destination.id == NULL_INDEX) {
            destinationId = travelDestinationDao.insertDestination(destination)
        } else {
            travelDestinationDao.updateDestination(destination)
            destinationId = destination.id.toLong()

            attractionDao.deleteAttractionsByDestinationId(destinationId.toInt())
            reviewDao.deleteReviewsByDestinationId(destinationId.toInt())
        }

        attractionDao.insertAttractions(
            attractions.map {
                it.copy(
                    id = NULL_INDEX,
                    destinationId = destinationId.toInt()
                )
            }
        )

        reviewDao.insertReviews(
            reviews.map {
                it.copy(
                    id = NULL_INDEX,
                    destinationId = destinationId.toInt()
                )
            }
        )
        return destinationId
    }

    override fun getFavoriteDestinations(): Flow<List<TravelDestinationWithDetails>> {
        return travelDestinationDao.getFavoriteDestinations()
    }

    override suspend fun toggleFavorite(
        id: Int,
        isFavorite: Boolean
    ) {
        travelDestinationDao.updateFavoriteStatus(
            id,
            isFavorite
        )
    }

    override suspend fun updateDestination(destination: TravelDestination) {
        travelDestinationDao.updateDestination(destination)
    }
}
