package com.khl_app.domain

import com.khl_app.domain.models.EventResponse
import com.khl_app.domain.models.LoginResponse
import com.khl_app.domain.models.TeamResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.Response
import java.util.UUID

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body credentials: Map<String, String>): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body credentials: Map<String, String>): Response<LoginResponse>

    @POST("auth/refresh")
    suspend fun refresh(@Header("Authorization") token: String): Response<LoginResponse>

    @GET("teams/get_events")
    suspend fun getEvents(
        @Query("start_time") start: Long?,
        @Query("end_time") end: Long?,
        @Query("teams") teams: List<String>
    ): Response<List<EventResponse>>

    @GET("teams/get_teams")
    suspend fun getTeams(): Response<List<TeamResponse>>

    @GET("predict/get_predicts")
    suspend fun getPredictions(
        @Query("user_id") userId: String,
        @Query("start_time") start: Long?,
        @Query("end_time") end: Long?
    ): Response<Map<String, Map<String, String>>>
}