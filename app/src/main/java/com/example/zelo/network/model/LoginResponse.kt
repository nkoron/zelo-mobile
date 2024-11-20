package com.example.zelo.network.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String
)