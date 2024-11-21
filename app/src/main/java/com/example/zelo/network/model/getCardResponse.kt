package com.example.zelo.network.model

import kotlinx.serialization.Serializable

@Serializable
data class getCardsResponse (
    val cards : List<Card>
)