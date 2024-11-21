package com.example.zelo.network.model

import kotlinx.serialization.Serializable

@Serializable
data class VerificationCodeRequest(
    val code: String
)
