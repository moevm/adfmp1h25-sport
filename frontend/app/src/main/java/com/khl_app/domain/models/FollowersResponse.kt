package com.khl_app.domain.models

import com.google.gson.annotations.SerializedName

data class FollowerResponse(
    val id: String,
    val level: Int,
    val name: String,
    val avatar: String?,
    val points: Int,
    val stats: FollowerStats
)

data class FollowerStats(
    @SerializedName("followers_count") val followersCount: Int,
    @SerializedName("following_count") val followingCount: Int,
    @SerializedName("predicted_games") val predictedGames: Int,
    @SerializedName("score_points") val scorePoints: Int,
    @SerializedName("winner_points") val winnerPoints: Int
)