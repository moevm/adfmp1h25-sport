package com.khl_app.domain.models

import android.media.Image

data class TeamResponse(
    val conference: String,
    val conferenceKey: ConferenceKey,
    val division: String,
    val divisionKey: String,
    val id: String,
    val image: Image,
    val khlID: String,
    val location: String,
    val name: String,
    )

enum class ConferenceKey(val key: String) {
    WEST("west"),
    EAST("east"),
    SOUTH("south"),
    NORTH("north"),
}