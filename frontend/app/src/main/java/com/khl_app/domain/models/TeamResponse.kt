package com.khl_app.domain.models


data class TeamResponse(
    val conference: String,
    val conferenceKey: ConferenceKey,
    val division: String,
    val divisionKey: String,
    val id: String,
    val image: String,
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