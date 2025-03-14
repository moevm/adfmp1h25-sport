package com.khl_app.domain.storage

import kotlinx.coroutines.flow.Flow

interface IRepository<T> {
    suspend fun saveInfo(info: T)
    fun getInfo(): Flow<T>
}