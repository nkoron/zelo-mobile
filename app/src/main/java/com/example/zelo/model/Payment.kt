package com.example.zelo.model

import java.util.UUID

data class Payment(
    val id: Int,
    val amount: Double,
    val type: String,
    val balanceBefore: Double,
    val balanceAfter: Double,
    val pending: Boolean,
    val linkUUID: UUID,
    val createdAt: String,
    val updatedAt: String,
    val card: Card,
)
