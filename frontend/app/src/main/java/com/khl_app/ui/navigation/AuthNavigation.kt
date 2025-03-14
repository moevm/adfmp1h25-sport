package com.khl_app.ui.navigation

import MainViewModel
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.khl_app.ui.screens.auth.LoginScreen
import com.khl_app.ui.screens.auth.RegisterScreen

fun NavGraphBuilder.authNavigation(navHostController: NavHostController, viewModel: MainViewModel) {
    composable(
        route = Screen.LoginScreen.route
    ) {
        LoginScreen(
            modifier = Modifier,
            viewModel = viewModel,
            onLogin = {
                navHostController.navigate(Screen.MainScreen.route) {
                    popUpTo(Screen.LoginScreen.route) {
                        inclusive = true
                    }
                }
            },
            onRegistration = {
                navHostController.navigate(Screen.RegistrationScreen.route) {
                    popUpTo(Screen.LoginScreen.route) {
                        inclusive = true
                    }
                }
            }
        )
    }
    composable(
        route = Screen.RegistrationScreen.route
    ) {
        RegisterScreen(
            modifier = Modifier,
            viewModel = viewModel,
            onLogin = {
                navHostController.navigate(Screen.LoginScreen.route) {
                    popUpTo(Screen.RegistrationScreen.route) {
                        inclusive = true
                    }
                }
            },
            onRegistration = {
                navHostController.navigate(Screen.MainScreen.route) {
                    popUpTo(Screen.RegistrationScreen.route) {
                        inclusive = true
                    }
                }
            }
        )
    }
}