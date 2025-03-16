package com.khl_app.domain.models

import com.google.gson.annotations.SerializedName
import java.security.Timestamp
import java.util.Date

data class EventWrapper(
    val event: EventResponse
)

data class EventResponse(
    val id: Int,
    val period: Long?,
    val score: String,
    @SerializedName("start_at") val startAt: Long,
    @SerializedName("start_at_day") val startAtDay: Long,
    @SerializedName("team_a") val teamA: TeamId,
    @SerializedName("team_b") val teamB: TeamId,
) {
    val timestamp: Date
        get() = Date(startAt)
}

data class TeamId(
    val id: Int
)