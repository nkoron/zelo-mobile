package com.example.zelo.network.model

import kotlinx.serialization.Serializable

@Serializable
data class getPaymentResponse (
    val payments: List<Payment>
)