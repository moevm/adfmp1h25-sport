package com.khl_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.khl_app.ui.view_models.MainViewModel


@Composable
fun AppNavGraph(navHostController: NavHostController, viewModel: MainViewModel) {
    
    NavHost(navController = navHostController, startDestination = Screen.LoginScreen.route) {
        authNavigation(navHostController, viewModel)
//        aboutNavigation(navHostController)
        clientNavigation(navHostController, viewModel)
    }
}