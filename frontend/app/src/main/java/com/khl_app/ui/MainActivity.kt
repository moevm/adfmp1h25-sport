package com.khl_app.ui

import MainViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

}
