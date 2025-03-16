package com.khl_app.ui.navigation

import MainViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.khl_app.presentation.screens.FollowersScreen
import com.khl_app.ui.screens.client.MainScreen
import com.khl_app.ui.screens.client.ProfileScreen

fun NavGraphBuilder.clientNavigation(navHostController: NavHostController, viewModel: MainViewModel) {
    composable(
        route = Screen.MainScreen.route
    ) {
        MainScreen(viewModel, navHostController)
    }
    composable(
        route = Screen.ProfileScreen.route
    ){
        ProfileScreen(viewModel, navHostController)
    }
    // Add this composable for TrackableScreen
    composable(
        route = Screen.TrackableScreen.route
    ){
        FollowersScreen(viewModel.followersViewModel, navHostController, viewModel)
    }
}