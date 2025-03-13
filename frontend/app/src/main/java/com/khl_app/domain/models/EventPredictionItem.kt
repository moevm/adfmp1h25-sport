package com.khl_app.domain.models

data class EventPredictionItem(
    val teamA: Team,
    val teamB: Team,
    val date: String,
    val time: String,
    val prediction: String?,
    val result: String?,
)

data class Team (
    val logo: String,
    val name: String,
)
