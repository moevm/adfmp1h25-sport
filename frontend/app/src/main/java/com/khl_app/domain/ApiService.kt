package com.khl_app.domain

import com.khl_app.domain.models.EventResponse
import com.khl_app.domain.models.EventWrapper
import com.khl_app.domain.models.LoginResponse
import com.khl_app.domain.models.TeamResponse
import com.khl_app.domain.models.TeamWrapper
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

    @GET("auth/is_token_valid")
    suspend fun isTokenValid(@Header("Authorization") token: String): Response<Map<String, String>>

    @GET("teams/get_events")
    suspend fun getEvents(
        @Header("Authorization") token: String,
        @Query("start_time") start: Long?,
        @Query("end_time") end: Long?,
        @Query("teams") teams: List<String>
    ): Response<List<EventWrapper>>

    @GET("teams/get_teams")
    suspend fun getTeams(
        @Header("Authorization") token: String
    ): Response<List<TeamWrapper>>

    @GET("predict/get_predicts")
    suspend fun getPredictions(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
        @Query("start_time") start: Long?,
        @Query("end_time") end: Long?
    ): Response<Map<String, Map<String, String>>?>

    @GET("predict/predict")
    suspend fun postPredict(
        @Header("Authorization") token: String,
        @Query("user_id") userId: String,
        @Query("score") score: String,
        @Query("event") eventId: String
    ): Response<String>
}