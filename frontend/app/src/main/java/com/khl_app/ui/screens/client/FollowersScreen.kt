package com.khl_app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.khl_app.ui.navigation.Screen
import com.khl_app.ui.presentation.FollowersUiState
import com.khl_app.ui.presentation.FollowersViewModel
import com.khl_app.ui.screens.client.BottomPanel
import com.khl_app.ui.screens.client.FollowerItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowersScreen(
    viewModel: FollowersViewModel,
    navHostController: NavHostController
    //onNavigateToMenu: () -> Unit,
    //onAddFollower: () -> Unit,
    //onViewProfile: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val followers by viewModel.followers.collectAsState()
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Отслеживаемые",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            bottomSheetState.show()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Меню"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E1E1E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF282828)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF282828))
        ) {
            when (uiState) {
                is FollowersUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }

                is FollowersUiState.Error -> {
                    val errorMessage = (uiState as FollowersUiState.Error).message
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadFollowers() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Text("Повторить")
                        }
                    }
                }

                is FollowersUiState.Success -> {
                    if (followers.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Нет отслеживаемых пользователей",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(followers) { follower ->
                                FollowerItem(
                                    follower = follower,
                                    onMessageClick = { {  } },
                                    onDeleteClick = { viewModel.removeFollower(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (bottomSheetState.isVisible) {
        BottomPanel(
            onCalendar = {navHostController.navigate(Screen.MainScreen.route)},
            onTrackable = {},
            onProfile = {
                navHostController.navigate(Screen.ProfileScreen.route)
            },
            onLogout = {},
            bottomSheetState,
            scope
        )
    }
}