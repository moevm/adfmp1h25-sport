package com.khl_app.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.khl_app.domain.storage.IRepository
import com.khl_app.storage.models.TokenData
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("tokens_store")

class TokenPreferenceRepository(val context: Context) : IRepository<TokenData> {
    override suspend fun saveInfo(info: TokenData) {
        context.dataStore.edit { pref ->
            pref[stringPreferencesKey("access_token")] = info.accessToken
            pref[stringPreferencesKey("refresh_token")] = info.refreshToken
        }
    }

    override fun getInfo() = context.dataStore.data.map { pref ->
        return@map TokenData(
            pref[stringPreferencesKey("access_token")] ?: "",
            pref[stringPreferencesKey("refresh_token")] ?: "",
        )
    }
}
