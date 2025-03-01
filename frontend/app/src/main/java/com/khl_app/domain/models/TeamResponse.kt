package com.khl_app.domain.models

import android.media.Image
import java.util.UUID

data class TeamResponse(
    val conference: String,
    val conferenceKey: ConferenceKey,
    val division: String,
    val divisionKey: String,
    val id: UUID,
    val image: Image,
    val khlID: UUID,
    val location: String,
    val name: String,
    )

enum class ConferenceKey(val key: String) {
    WEST("west"),
    EAST("east"),
    SOUTH("south"),
    NORTH("north"),
}