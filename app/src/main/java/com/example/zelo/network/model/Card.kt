package com.example.zelo.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val id: Int?,
    val number: String,
    val expirationDate: String,
    val fullName: String,
    val type: String,
    val cvv: String?= null, //chequear
    val createdAt: String?= null,
    val updatedAt: String?= null
)
