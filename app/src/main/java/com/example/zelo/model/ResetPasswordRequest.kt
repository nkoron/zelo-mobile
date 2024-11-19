package com.example.zelo.model

data class ResetPasswordRequest(
    val code: String,
    val password: String
)