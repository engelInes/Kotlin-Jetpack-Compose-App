package com.inesengel.travelapp.core.model

import project.model.TravelType

data class AddDestinationFormState(
    val name: String = "",
    val type: TravelType = TravelType.ADVENTURE,
    val country: String = "",
    val price: String = "",
    val duration: String = "",
    val attractions: List<DestinationAttraction> = emptyList(),
    val reviews: List<UserReview> = emptyList(),
    val isDataValid: Boolean = false
)
