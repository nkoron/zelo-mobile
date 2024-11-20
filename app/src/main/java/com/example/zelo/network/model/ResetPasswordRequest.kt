package com.example.zelo.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    val code: String,
    val password: String
)