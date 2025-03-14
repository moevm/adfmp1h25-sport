// SplashNavigation.kt
package com.khl_app.ui.navigation

import MainViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.khl_app.ui.screens.splash.SplashScreen

fun NavGraphBuilder.splashNavigation(navHostController: NavHostController, viewModel: MainViewModel) {
    composable(
        route = Screen.SplashScreen.route
    ) {
        SplashScreen(
            viewModel = viewModel,
            onNavigateToLogin = {
                navHostController.navigate(Screen.LoginScreen.route) {
                    popUpTo(Screen.SplashScreen.route) {
                        inclusive = true
                    }
                }
            },
            onNavigateToMain = {
                navHostController.navigate(Screen.MainScreen.route) {
                    popUpTo(Screen.SplashScreen.route) {
                        inclusive = true
                    }
                }
            }
        )
    }
}