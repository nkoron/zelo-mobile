package com.example.zelo.model

import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val id: Int?,
    val number: Int,
    val expirationDate: String,
    val fullName: String,
    val type: String,
    val cvv: Int?, //chequear
    val createdAt: String?,
    val updatedAt: String?

)
