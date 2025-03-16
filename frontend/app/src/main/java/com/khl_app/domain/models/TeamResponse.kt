package com.khl_app.domain.models

import com.google.gson.annotations.SerializedName

data class TeamWrapper(
    val team: TeamResponse
)

data class TeamResponse(
    val conference: String,
    @SerializedName("conference_key") val conferenceKey: String,
    val division: String,
    @SerializedName("division_key") val divisionKey: String,
    val id: Int,
    val image: String,
    @SerializedName("khl_id") val khlID: String,
    val location: String,
    val name: String,
)

//enum class ConferenceKey(val key: String) {
//    WEST("west"),
//    EAST("east"),
//    SOUTH("south"),
//    NORTH("north"),
//}