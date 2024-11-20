package com.example.zelo.network.model

import kotlinx.serialization.Serializable

@Serializable
sealed class PaymentRequest

@Serializable
data class BalancePaymentRequest(
    val receiverEmail: String? = null,
    val amount: Int,
    val description: String,
) : PaymentRequest()

@Serializable
data class CardPaymentRequest(
    val cardId: Int,
    val receiverEmail: String? = null,
    val amount: Int?,
    val description: String,
) : PaymentRequest()

@Serializable
data class LinkPaymentRequest(
    val amount: Int,
    val description: String,
) : PaymentRequest()

@Serializable
data class PaymentIdRequest(
    val id: Int
)


