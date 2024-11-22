package com.example.zelo.network.model

import kotlinx.serialization.Serializable

@Serializable
data class PaymentResponse (
    val newBalance: Double? = null,
    val linkUuid: String? = null,
    val message: String? = null,


)