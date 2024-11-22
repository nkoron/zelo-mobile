package com.example.zelo.network.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class Payment(
    val id: Int,
    val amount: Double,
    val type: String,
    val balanceBefore: Double,
    val balanceAfter: Double,
    val pending: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val card: Card?,
    val payer: User,
    val receiver: User
)

@Serializable
data class LinkPayment(
    val linkUuid: String,
)
