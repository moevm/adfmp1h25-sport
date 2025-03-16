// SplashScreen.kt
package com.khl_app.ui.screens.splash

import MainViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()

        LaunchedEffect(key1 = true) {
            viewModel.checkTokenValidity { isValid ->
                if (isValid) {
                    onNavigateToMain()
                } else {
                    onNavigateToLogin()
                }
            }
        }
    }
}