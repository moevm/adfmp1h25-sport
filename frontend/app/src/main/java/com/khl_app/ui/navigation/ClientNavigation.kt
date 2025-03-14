package com.khl_app.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.khl_app.ui.screens.client.MainScreen
import com.khl_app.ui.view_models.MainViewModel

fun NavGraphBuilder.clientNavigation(navHostController: NavHostController, viewModel: MainViewModel) {
    composable(
        route = Screen.MainScreen.route
    ) {
        MainScreen(viewModel)
    }
}