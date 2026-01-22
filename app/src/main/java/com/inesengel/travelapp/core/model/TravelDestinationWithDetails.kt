package com.inesengel.travelapp.core.model

import androidx.room.Embedded
import androidx.room.Relation
import com.inesengel.travelapp.infra.utils.Constants.Database.DESTINATION_ID_KEY
import com.inesengel.travelapp.infra.utils.Constants.Database.PARENT_TABLE_ID_KEY
import project.model.TravelDestination

data class TravelDestinationWithDetails(
    @Embedded val destination: TravelDestination,

    @Relation(
        parentColumn = PARENT_TABLE_ID_KEY,
        entityColumn = DESTINATION_ID_KEY
    )
    val attractions: List<DestinationAttraction>,

    @Relation(
        parentColumn = PARENT_TABLE_ID_KEY,
        entityColumn = DESTINATION_ID_KEY
    )
    val reviews: List<UserReview>
)