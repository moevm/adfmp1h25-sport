package com.khl_app.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.khl_app.domain.storage.IRepository
import com.khl_app.storage.models.TeamData
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("team_store")

class TeamPreferenceRepository (
    val context: Context
) : IRepository<TeamData>  {
    override suspend fun saveInfo(info: TeamData) {
        context.dataStore.edit { pref ->
            pref[stringPreferencesKey("conference")] = info.conference
            pref[stringPreferencesKey("conference_key")] = info.conferenceKey
            pref[stringPreferencesKey("id")] = info.id.toString()
            pref[stringPreferencesKey("division")] = info.division
            pref[stringPreferencesKey("division_key")] = info.divisionKey
            pref[stringPreferencesKey("image")] = info.image
            pref[stringPreferencesKey("khl_id")] = info.khlID
            pref[stringPreferencesKey("location")] = info.location
            pref[stringPreferencesKey("name")] = info.name
        }
    }

    override fun getInfo() = context.dataStore.data.map { pref ->
        return@map TeamData(
            pref[stringPreferencesKey("conference")] ?: "",
            pref[stringPreferencesKey("conference_key")] ?: "",
            pref[stringPreferencesKey("division")] ?: "",
            pref[stringPreferencesKey("division_key")] ?: "",
            (pref[stringPreferencesKey("id")] ?: "").toIntOrNull() ?: 0,
            pref[stringPreferencesKey("image")] ?: "",
            pref[stringPreferencesKey("khl_id")] ?: "",
            pref[stringPreferencesKey("location")] ?: "",
            pref[stringPreferencesKey("name")] ?: ""
        )
    }

    override suspend fun deleteInfo() {
        context.dataStore.edit {
            it.clear()
        }
    }
}