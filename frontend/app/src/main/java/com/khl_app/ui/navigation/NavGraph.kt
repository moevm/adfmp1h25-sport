package com.khl_app.ui.navigation

import MainViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.khl_app.ui.screens.ProfileScreen

@Composable
fun AppNavGraph(navHostController: NavHostController, viewModel: MainViewModel) {
    NavHost(navController = navHostController, startDestination = Screen.LoginScreen.route) {
        authNavigation(navHostController, viewModel)
        clientNavigation(navHostController, viewModel)
        composable(Screen.ProfileScreen.route) {
            ProfileScreen(viewModel = viewModel)
        }
    }
}