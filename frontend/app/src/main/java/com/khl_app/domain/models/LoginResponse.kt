package com.khl_app.domain.models

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)
