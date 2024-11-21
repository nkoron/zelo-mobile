package com.example.zelo.network

import kotlinx.serialization.Serializable

@Serializable
data class NetworkError (
    val message: String
)