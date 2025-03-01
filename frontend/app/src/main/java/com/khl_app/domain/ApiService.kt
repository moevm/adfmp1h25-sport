package com.khl_app.domain

import com.khl_app.domain.models.EventResponse
import com.khl_app.domain.models.LoginResponse
import com.khl_app.domain.models.TeamResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    fun login(@Body credentials: Map<String, String>): Call<LoginResponse>
    @POST("auth/refresh")
    fun refresh(@Header("Authorization") token: String): Call<LoginResponse>
    @GET("teams/get_events")
    fun getEvents(): Call<EventResponse>
    @GET("teams/get_teams")
    fun getTeams(): Call<TeamResponse>
}