package com.example.zelo.network.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterUser (
    val firstName: String,
    val lastName: String,
    val email: String,
    val birthDate: String,
    val password: String
)