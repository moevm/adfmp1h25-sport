package com.khl_app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.khl_app.ui.navigation.Screen

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NavigationDrawer(navHostController: NavHostController) {
    val items = listOf(Screen.MainScreen, Screen.ProfileScreen, Screen.SettingsScreen)
    var selectedItem by remember { mutableStateOf(items[0]) }

    ModalDrawer(
        drawerContent = {
            items.forEach { item ->
                ListItem(
                    text = { Text(item.route) },
                    modifier = Modifier.clickable {
                        selectedItem = item
                        navHostController.navigate(item.route) {
                            popUpTo(Screen.MainScreen.route)
                        }
                    }
                )
            }
        },
        content = {
            // Main content
        }
    )
}