package com.khl_app.ui.navigation

import MainViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.khl_app.presentation.screens.FollowersScreen
import com.khl_app.ui.screens.client.MainScreen
import com.khl_app.ui.screens.client.ProfileScreen

fun NavGraphBuilder.clientNavigation(navHostController: NavHostController, viewModel: MainViewModel) {
    composable(
        route = Screen.MainScreen.route,
        arguments = listOf(
            navArgument("isFromMenu") {
                type = NavType.BoolType
                defaultValue = true
            },
            navArgument("name") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
            navArgument("canRedact") {
                type = NavType.BoolType
                defaultValue = true
            }
        )
    ) { backStackEntry ->
        val isFromMenu = backStackEntry.arguments?.getBoolean("isFromMenu") ?: true
        val name = backStackEntry.arguments?.getString("name")
        val canRedact = backStackEntry.arguments?.getBoolean("canRedact") ?: true

        MainScreen(
            viewModel = viewModel,
            navHostController = navHostController,
            isFromMenu = isFromMenu,
            name = name,
            canRedact = canRedact
        )
    }

    composable(
        route = Screen.ProfileScreen.route,
        arguments = listOf(
            navArgument("userId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
            navArgument("isFromMenu") {
                type = NavType.BoolType
                defaultValue = true
            }
        )
    ) { backStackEntry ->
        val userId = backStackEntry.arguments?.getString("userId")
        val isFromMenu = backStackEntry.arguments?.getBoolean("isFromMenu") ?: true

        ProfileScreen(
            viewModel = viewModel,
            followersViewModel = viewModel.followersViewModel,
            navHostController = navHostController,
            userId = userId,
            isFromMenu = isFromMenu,
        )
    }

    // Add this composable for TrackableScreen
    composable(
        route = Screen.TrackableScreen.route
    ) {
        FollowersScreen(viewModel.followersViewModel, viewModel, navHostController, viewModel)
    }
}