package com.khl_app.domain.models

import java.security.Timestamp
import java.util.Date
import java.util.UUID

data class EventResponse(
    val id: UUID,
    val period: Long,
    val score: String,
    val startAt: Timestamp,
    val startAtDay: Date,
    val teamA: Team,
    val teamB: Team,
)

data class Team (
    val id: UUID,
)