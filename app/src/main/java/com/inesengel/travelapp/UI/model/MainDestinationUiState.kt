package com.inesengel.travelapp.UI.model

import project.model.TravelDestination
import project.model.TravelType

data class MainDestinationUiState(
    val destinations: List<TravelDestination> = emptyList(),
    val selectedType: TravelType? = null,
    val isLoading: Boolean = false,
    val logout: Boolean = false,
    val destinationOptionsMenu: Int? = null,
    val showDeleteMessage: Boolean = false,
    val userName: String = "",
    val userEmail: String = ""
)