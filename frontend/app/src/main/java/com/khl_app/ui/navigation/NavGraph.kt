// NavGraph.kt
package com.khl_app.ui.navigation

import MainViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun AppNavGraph(navHostController: NavHostController, viewModel: MainViewModel) {
    NavHost(navController = navHostController, startDestination = Screen.SplashScreen.route) {
        splashNavigation(navHostController, viewModel)
        authNavigation(navHostController, viewModel)
//        aboutNavigation(navHostController)
        clientNavigation(navHostController, viewModel)
    }
}