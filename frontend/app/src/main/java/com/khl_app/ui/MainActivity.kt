package com.khl_app.ui

import MainViewModel
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.khl_app.storage.TeamPreferenceRepository
import com.khl_app.storage.TokenPreferenceRepository
import com.khl_app.ui.navigation.AppNavGraph
import com.khl_app.ui.themes.KhlAppTheme


class MainActivity : ComponentActivity() {
    private val tokenCache = TokenPreferenceRepository(this)
    private val teamCache = TeamPreferenceRepository(this)
    private val viewModel by viewModels<MainViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(applicationContext, tokenCache, teamCache) as T
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KhlAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navHostController = rememberNavController()
                    AppNavGraph(navHostController, viewModel)
                }
            }
        }
    }

//    private fun loadTeams(): List<TeamResponse> {
//        var teams: List<TeamResponse> = emptyList()
//        ApiClient.apiService.getTeams().enqueue(object : Callback<List<TeamResponse>> {
//            override fun onResponse(
//                call: Call<List<TeamResponse>>,
//                response: Response<List<TeamResponse>>
//            ) {
//                if (response.isSuccessful) {
//                    teams = response.body() ?: emptyList()
//                } else {
//                    throw Exception("Unsuccessful response")
//                }
//            }
//
//            override fun onFailure(call: Call<List<TeamResponse>>, t: Throwable) {
//
//            }
//        })
//        return teams
//    }
//
//    private fun loadEvents(teams: List<String>, start: Long?, end: Long?): List<EventResponse> {
//        var events: List<EventResponse> = emptyList()
//        ApiClient.apiService.getEvents(start, end, teams)
//            .enqueue(object : Callback<List<EventResponse>> {
//                override fun onResponse(
//                    call: Call<List<EventResponse>>,
//                    response: Response<List<EventResponse>>
//                ) {
//                    if (response.isSuccessful) {
//                        events = response.body() ?: emptyList()
//                    } else {
//                        throw Exception("Unsuccessful response")
//                    }
//                }
//
//                override fun onFailure(call: Call<List<EventResponse>>, t: Throwable) {
//
//                }
//            })
//        return events
//    }
//
//    private fun loadPredictions(
//        userId: String,
//        start: Long?,
//        end: Long?
//    ): Map<String, Map<String, String>> {
//        var predictions: Map<String, Map<String, String>> = emptyMap()
//        ApiClient.apiService.getPredictions(userId, start, end)
//            .enqueue(object : Callback<Map<String, Map<String, String>>> {
//                override fun onResponse(
//                    call: Call<Map<String, Map<String, String>>>,
//                    response: Response<Map<String, Map<String, String>>>
//                ) {
//                    if (response.isSuccessful) {
//                        predictions = response.body() ?: emptyMap()
//                    } else {
//                        throw Exception("Unsuccessful response")
//                    }
//                }
//
//                override fun onFailure(call: Call<Map<String, Map<String, String>>>, t: Throwable) {
//
//                }
//            })
//
//        return predictions
//    }
//
//    private fun cacheTeams(teams: List<TeamResponse>) {
//        val gson = Gson()
//        val json = gson.toJson(teams)
//        sharedPreferences.edit().putString("teams", json).apply()
//    }
//
//    private fun getCachedTeams(): List<TeamResponse> {
//        val gson = Gson()
//        val json = sharedPreferences.getString("teams", null)
//        val type = object : TypeToken<List<TeamResponse>>() {}.type
//
//        return gson.fromJson(json, type)
//    }
//
//    private fun loadAndMergeInfo(
//        start: Long?,
//        end: Long?,
//    ) {
//        var teams = getCachedTeams()
//        if (teams.isEmpty()) {
//            teams = loadTeams()
//            cacheTeams(teams)
//        }
//        val listIDs: MutableList<String> = mutableListOf()
//        for (t in teams) {
//            listIDs.add(t.id)
//        }
//        val events = loadEvents(listIDs, start, end)
//        val token = sharedPreferencesTokens.getString("access_token", null)
//        val userID: String?
//        try {
//            val decodedJWT: DecodedJWT = JWT.decode(token)
//            userID = decodedJWT.getClaim("id").asString()
//        } catch (e: Exception) {
//            throw Exception(e)
//        }
//        if (userID.isNullOrEmpty()) {
//            throw Exception("Null user id")
//        }
//        val predictions = loadPredictions(userID, start, end)
//        merge(teams, events, predictions)
//    }
//
//    private fun merge(
//        t: List<TeamResponse>,
//        e: List<EventResponse>,
//        p: Map<String, Map<String, String>>
//    ) {
//
//    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KhlAppTheme {
        NavHost(rememberNavController(), viewModel())
    }
}