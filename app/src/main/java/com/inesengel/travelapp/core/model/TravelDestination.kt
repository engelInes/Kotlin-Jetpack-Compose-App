package project.model

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.inesengel.travelapp.infra.utils.Constants.Database.DESTINATIONS_TABLE_NAME

enum class TravelType {
    ADVENTURE, CRUISE, BUSINESS, FAMILY_TRIP, CITY_BREAK
}

@Entity(tableName = DESTINATIONS_TABLE_NAME)
data class TravelDestination(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: TravelType,
    val price: Double,
    val rating: Int,
    val country: String,
    val visited: Boolean,
    val isFavorite: Boolean = false,
    val duration: Int,
    @DrawableRes val imageResId: Int,
    val imagePath: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)