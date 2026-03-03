package com.inesengel.travelapp.core.model

data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val keepLoggedIn: Boolean = false,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)
