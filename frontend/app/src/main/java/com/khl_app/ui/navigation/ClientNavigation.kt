package com.khl_app.ui.navigation

import MainViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.khl_app.ui.screens.client.MainScreen

fun NavGraphBuilder.clientNavigation(navHostController: NavHostController, viewModel: MainViewModel) {
    composable(
        route = Screen.MainScreen.route
    ) {
        MainScreen(viewModel)
    }
}