package com.khl_app.domain

import com.khl_app.domain.models.EventResponse
import com.khl_app.domain.models.LoginResponse
import com.khl_app.domain.models.TeamResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.UUID

interface ApiService {
    @POST("auth/login")
    fun login(@Body credentials: Map<String, String>): Call<LoginResponse>

    @POST("auth/refresh")
    fun refresh(@Header("Authorization") token: String): Call<LoginResponse>

    @GET("teams/get_events")
    fun getEvents(
        @Query("start_time") start: Long?,
        @Query("end_time") end: Long?,
        @Query("teams") teams: List<String>
    ): Call<List<EventResponse>>

    @GET("teams/get_teams")
    fun getTeams(): Call<List<TeamResponse>>

    @GET("predict/get_predicts")
    fun getPredictions(
        @Query("user_id") userId: String,
        @Query("start_time") start: Long?,
        @Query("end_time") end: Long?
    ): Call<Map<String, Map<String, String>>>
}