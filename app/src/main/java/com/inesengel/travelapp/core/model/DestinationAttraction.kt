package com.inesengel.travelapp.core.model

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.inesengel.travelapp.infra.utils.Constants.Database.ATTRACTIONS_TABLE_NAME
import com.inesengel.travelapp.infra.utils.Constants.Database.DESTINATION_ID_KEY
import com.inesengel.travelapp.infra.utils.Constants.Database.PARENT_TABLE_ID_KEY
import project.model.TravelDestination

@Entity(
    tableName = ATTRACTIONS_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = TravelDestination::class,
            parentColumns = [PARENT_TABLE_ID_KEY],
            childColumns = [DESTINATION_ID_KEY],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(DESTINATION_ID_KEY)]
)
data class DestinationAttraction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val destinationId: Int,
    val name: String,
    val description: String,
    val duration: Int,
    @DrawableRes val imageResId: Int,
    val imagePath: String? = null
)