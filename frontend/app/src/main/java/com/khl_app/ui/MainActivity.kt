package com.khl_app.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.khl_app.R
import com.khl_app.domain.ApiClient
import com.khl_app.domain.models.EventResponse
import com.khl_app.domain.models.TeamResponse
import com.khl_app.ui.fragments.MenuBottomSheet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT

class MainActivity : AppCompatActivity() {
    private lateinit var settingsButton: ImageButton
    private lateinit var menuButton: ImageButton
    private lateinit var dateText: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesTokens: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // TODO add list of actions by get request
        sharedPreferences = getSharedPreferences("teamsCache", MODE_PRIVATE)
        sharedPreferencesTokens = getSharedPreferences("tokens", MODE_PRIVATE)
        settingsButton = findViewById(R.id.settings_btn)
        menuButton = findViewById(R.id.menu_btn)
        dateText = findViewById(R.id.date_info)

        settingsButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }
        menuButton.setOnClickListener {
            val menuBottomSheet = MenuBottomSheet()
            menuBottomSheet.show(supportFragmentManager, "menuBottomSheet")
        }
    }

    private fun loadTeams(): List<TeamResponse> {
        var teams: List<TeamResponse> = emptyList()
        ApiClient.apiService.getTeams().enqueue(object : Callback<List<TeamResponse>> {
            override fun onResponse(
                call: Call<List<TeamResponse>>,
                response: Response<List<TeamResponse>>
            ) {
                if (response.isSuccessful) {
                    teams = response.body() ?: emptyList()
                } else {
                    Toast.makeText(this@MainActivity, "Failed to load matches", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<List<TeamResponse>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
        return teams
    }

    private fun loadEvents(teams: List<String>, start: Long?, end: Long?): List<EventResponse> {
        var events: List<EventResponse> = emptyList()
        ApiClient.apiService.getEvents(start, end, teams)
            .enqueue(object : Callback<List<EventResponse>> {
                override fun onResponse(
                    call: Call<List<EventResponse>>,
                    response: Response<List<EventResponse>>
                ) {
                    if (response.isSuccessful) {
                        events = response.body() ?: emptyList()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Failed to load matches",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<EventResponse>>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        return events
    }

    private fun loadPredictions(userId: String, start: Long?, end: Long?) : Map<String, Map<String, String>> {
        var predictions: Map<String, Map<String, String>> = emptyMap()
        ApiClient.apiService.getPredictions(userId, start, end).enqueue(object: Callback<Map<String, Map<String, String>>> {
            override fun onResponse(
                call: Call<Map<String, Map<String, String>>>,
                response: Response<Map<String, Map<String, String>>>
            ) {
                if (response.isSuccessful) {
                    predictions = response.body()?: emptyMap()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to load matches",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Map<String, String>>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        return predictions
    }

    private fun cacheTeams(teams: List<TeamResponse>) {
        val gson = Gson()
        val json = gson.toJson(teams)
        sharedPreferences.edit().putString("teams", json).apply()
    }

    private fun getCachedTeams(): List<TeamResponse> {
        val gson = Gson()
        val json = sharedPreferences.getString("teams", null)
        val type = object : TypeToken<List<TeamResponse>>() {}.type

        return gson.fromJson(json, type)
    }

    private fun loadAndMergeInfo(start: Long?, end: Long?) {
        var teams = getCachedTeams()
        if (teams.isEmpty()) {
            teams = loadTeams()
            cacheTeams(teams)
        }
        val listIDs: MutableList<String> = mutableListOf()
        for (t in teams) {
            listIDs.add(t.id)
        }
        val events = loadEvents(listIDs, start, end)
        val token = sharedPreferencesTokens.getString("access_token", null)
        val userID: String?
        try {
            val decodedJWT : DecodedJWT = JWT.decode(token)
            userID = decodedJWT.getClaim("id").asString()
        } catch (e: Exception) {
            Toast.makeText(
                this@MainActivity,
                "Failed to parse token",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (userID.isNullOrEmpty()) {
            Toast.makeText(
                this@MainActivity,
                "User id not found",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val predictions = loadPredictions(userID, start, end)
//        merge(teams, events, predictions)
    }

//    private fun merge(t: List<TeamResponse>, e: List<EventResponse>, p: Map<String, Map<String, String>>) {
//
//    }
}