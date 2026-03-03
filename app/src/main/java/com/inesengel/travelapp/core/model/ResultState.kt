package com.inesengel.travelapp.core.model

sealed interface ResultState {
    data class Success(val username: String) : ResultState
    data class Error(val messageResId: Int) : ResultState
}
