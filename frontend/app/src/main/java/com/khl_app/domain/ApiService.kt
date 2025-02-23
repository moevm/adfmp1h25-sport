package com.khl_app.domain

import com.khl_app.domain.models.LoginResponse
import okhttp3.Credentials
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    fun login(@Body credentials: Map<String, String>): Call<LoginResponse>
}