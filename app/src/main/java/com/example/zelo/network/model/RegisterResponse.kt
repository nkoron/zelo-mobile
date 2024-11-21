package com.example.zelo.network.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse (
    val user: User?
)