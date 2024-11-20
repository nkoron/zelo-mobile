package com.example.zelo.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Error(
    val code: Int?,
    val message: String
)