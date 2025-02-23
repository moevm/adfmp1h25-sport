package com.khl_app.domain

import com.khl_app.domain.models.LoginResponse
import okhttp3.Credentials
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    fun login(@Body credentials: Map<String, String>): Call<LoginResponse>
    @POST("auth/refresh")
    fun refresh(@Header("Authorization") token: String): Call<LoginResponse>
}