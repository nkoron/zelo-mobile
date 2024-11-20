package com.example.zelo.network.model

import kotlinx.serialization.Serializable

@Serializable
data class BalanceResponse(
    val newBalance: Double
)
