package com.khl_app.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.khl_app.domain.ApiClient
import com.khl_app.domain.models.EventResponse
import com.khl_app.domain.models.TeamResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.khl_app.R
import com.khl_app.ui.themes.KhlAppTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesTokens: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        enableEdgeToEdge()
        sharedPreferences = getSharedPreferences("teamsCache", MODE_PRIVATE)
        sharedPreferencesTokens = getSharedPreferences("tokens", MODE_PRIVATE)
        setContent {
            KhlAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
//
//        settingsButton.setOnClickListener {
//            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
//        }
//        menuButton.setOnClickListener {
//            val menuBottomSheet = MenuBottomSheet()
//            menuBottomSheet.show(supportFragmentManager, "menuBottomSheet")
//        }
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

    private fun loadPredictions(
        userId: String,
        start: Long?,
        end: Long?
    ): Map<String, Map<String, String>> {
        var predictions: Map<String, Map<String, String>> = emptyMap()
        ApiClient.apiService.getPredictions(userId, start, end)
            .enqueue(object : Callback<Map<String, Map<String, String>>> {
                override fun onResponse(
                    call: Call<Map<String, Map<String, String>>>,
                    response: Response<Map<String, Map<String, String>>>
                ) {
                    if (response.isSuccessful) {
                        predictions = response.body() ?: emptyMap()
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
            val decodedJWT: DecodedJWT = JWT.decode(token)
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
        merge(teams, events, predictions)
    }

    private fun merge(
        t: List<TeamResponse>,
        e: List<EventResponse>,
        p: Map<String, Map<String, String>>
    ) {

    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            MainScreen()
        }
        composable("settings_screen") {
            SettingsScreen()
        }
    }
}

@Composable
fun MainScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopBar()
        Spacer(modifier = Modifier.height(20.dp))
        MatchList()
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MenuButton()
        CenterContent(modifier = Modifier
            .weight(1f)
            .padding(horizontal = 20.dp))
        SettingsButton()
    }
}

@Composable
fun CenterContent(modifier: Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Календарь",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.surface
        )
        Text(
            text = "сегодня: " + LocalDate.now().toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun MenuButton() {
    IconButton(onClick = { }) {
        Icon(
            Icons.Rounded.Menu,
            contentDescription = "Menu Button",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun SettingsButton() {
    IconButton(onClick = { /* Handle settings button click */ }) {
        Icon(
            Icons.Rounded.Settings,
            contentDescription = "Settings Button",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun MatchList() {
    // Здесь вы можете добавить RecyclerView или LazyColumn для отображения списка матчей
    LazyColumn {
        items(10) { index ->
            Text(text = "Match $index")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KhlAppTheme {
        MainScreen()
    }
}