package com.khl_app.domain

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.github.cdimascio.dotenv.dotenv
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

fun loadUrl(): String {
    val dotenv = dotenv {
        directory = "./"
        filename = "env"
    }
    return dotenv["BASE_URL"]
}

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:5000"

    private val gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .setLenient() // Более мягкий парсинг JSON
        .create()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS) // Таймаут подключения
        .readTimeout(20, TimeUnit.SECONDS)    // Таймаут чтения (увеличен)
        .writeTimeout(20, TimeUnit.SECONDS)   // Таймаут записи
        .retryOnConnectionFailure(true)       // Автоматические повторные попытки
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}