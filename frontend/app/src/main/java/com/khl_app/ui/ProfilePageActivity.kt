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

class ProfilePageActivity : ComponentActivity(){
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesTokens: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}