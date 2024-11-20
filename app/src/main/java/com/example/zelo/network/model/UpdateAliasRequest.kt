package com.example.zelo.network.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAliasRequest (
    val alias: String

)