package com.example.zelo.network.model

import kotlinx.serialization.Serializable

@Serializable
data class WalletDetails(
    val id: Int,
    val balance: Double,
    val invested: Double,
    val cbu: String,
    val alias: String,

)