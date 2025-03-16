package com.khl_app.domain.models

data class EventPredictionItem(
    val id: Int,
    val teamA: Team,
    val teamB: Team,
    val date: String,
    val time: String,
    val prediction: String?,
    val result: String?,
    val period: Long?
)

data class Team (
    val logo: String,
    val name: String,
)
