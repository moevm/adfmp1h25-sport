package com.khl_app.domain.models

import java.security.Timestamp
import java.util.Date

data class EventResponse(
    val id: String,
    val period: Long,
    val score: String,
    val startAt: Timestamp,
    val startAtDay: Date,
    val teamA: TeamId,
    val teamB: TeamId,
)

data class TeamId (
    val id: String,
)