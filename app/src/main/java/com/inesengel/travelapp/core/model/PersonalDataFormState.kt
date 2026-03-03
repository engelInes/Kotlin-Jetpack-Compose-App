package com.inesengel.travelapp.core.model

data class PersonalDataFormState(
    val usernameError: Int? = null,
    val emailError: Int? = null,
    val isDataValid: Boolean = false
)
