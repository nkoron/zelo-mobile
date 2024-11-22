package com.example.zelo.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Payment(
    val id: Int? = null,
    val amount: Double? = null,
    val type: String? = null,
    val balanceBefore: Double? = null,
    val balanceAfter: Double? = null,
    val pending: Boolean? = null,
    val linkUUID: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val card: Card? = null,
    val payer: User? = null,
    val receiver: User? = null
)
