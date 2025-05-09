package com.khl_app.presentation.screens

import MainViewModel
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.khl_app.storage.getUserIdFromToken
import com.khl_app.ui.navigation.Screen
import com.khl_app.ui.presentation.FollowersUiState
import com.khl_app.ui.presentation.FollowersViewModel
import com.khl_app.ui.presentation.SubscriptionState
import com.khl_app.ui.screens.AboutPopUp
import com.khl_app.ui.screens.client.BottomPanel
import com.khl_app.ui.screens.client.FollowerItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowersScreen(
    viewModel: FollowersViewModel,
    mainViewModel: MainViewModel,
    navHostController: NavHostController,
    mainModel: MainViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val followers by viewModel.followers.collectAsState()
    val subscriptionState by viewModel.subscriptionState.collectAsState()

    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var isAboutVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var showSubscribeDialog by remember { mutableStateOf(false) }
    var userIdInput by remember { mutableStateOf("") }
    var ownUserId by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        mainViewModel.tokenCache.getInfo().collect { tokenData ->
            ownUserId = getUserIdFromToken(tokenData.accessToken)!!
        }
    }

    LaunchedEffect(subscriptionState) {
        when (subscriptionState) {
            is SubscriptionState.Success -> {
                Toast.makeText(
                    context,
                    (subscriptionState as SubscriptionState.Success).message,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetSubscriptionState()
            }
            is SubscriptionState.Error -> {
                Toast.makeText(
                    context,
                    (subscriptionState as SubscriptionState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetSubscriptionState()
            }
            else -> {}
        }
    }

    if (showSubscribeDialog) {
        AlertDialog(
            onDismissRequest = {
                showSubscribeDialog = false
                userIdInput = ""
            },
            title = {
                Text(
                    "Подписаться на пользователя",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = userIdInput,
                    onValueChange = { userIdInput = it },
                    label = { Text("ID пользователя", color = Color.White.copy(alpha = 0.7f)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        focusedLabelColor = Color.White
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (userIdInput.isNotEmpty()) {
                            viewModel.subscribeToUser(userIdInput)
                            showSubscribeDialog = false
                            userIdInput = ""
                        } else {
                            Toast.makeText(
                                context,
                                "Введите ID пользователя",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6C5CE7)
                    )
                ) {
                    Text("Подписаться", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSubscribeDialog = false
                        userIdInput = ""
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text("Отмена")
                }
            },
            containerColor = Color(0xFF2C2F3E),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

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
                    IconButton(onClick = { showSubscribeDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2C2F3E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White,
                )
            )
        },
        containerColor = Color(0xFF2C2F3E)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = Color(0xFF2C2F3E))
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
                        // В блоке LazyColumn в FollowersScreen.kt, где выводится список подписчиков
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            // Сортируем подписчиков по количеству очков в убывающем порядке
                            val sortedFollowers = followers.sortedByDescending { it.points }

                            items(sortedFollowers) { follower ->
                                FollowerItem(
                                    follower = follower,
                                    onMessageClick = {
                                        Log.d("FollowersList", "Message click for follower: ${follower.id}")
                                        mainViewModel.eventViewModel.setUserId(follower.id)
                                        mainViewModel.eventViewModel.resetAndLoadEvents()
                                        navHostController.navigate(Screen.MainScreen.createRoute(canRedact = false, isFromMenu = false, name = follower.name))
                                    },
                                    onDeleteClick = { viewModel.removeFollower(it) },
                                    onItemClick = {
                                        viewModel.viewModelScope.launch {
                                            val my_id = getUserIdFromToken(mainViewModel.tokenCache.getInfo().first().accessToken)
                                            navHostController.navigate(Screen.ProfileScreen.createRoute(
                                                userId = follower.id,
                                                isFromMenu = false,
                                                isYou = follower.id == my_id
                                            ))
                                        }
                                    },
                                    isYou = follower.id == ownUserId,
                                    isTopFollower = sortedFollowers.indexOf(follower) == 0
                                )
                            }
                        }
                    }
                }
            }

            if (subscriptionState is SubscriptionState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color(0xFF2C2F3E)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }

    if (bottomSheetState.isVisible) {
        BottomPanel(
            onCalendar = {
                mainViewModel.eventViewModel.setUserId("current")
                mainViewModel.eventViewModel.resetAndLoadEvents()
                navHostController.navigate(Screen.MainScreen.route)
            },
            onTrackable = {},
            onProfile = {
                scope.launch {
                    val tokenData = mainViewModel.tokenCache.getInfo().first()
                    navHostController.navigate(Screen.ProfileScreen.createRoute(
                        userId = getUserIdFromToken(tokenData.accessToken),
                        isYou = true
                    ))
                }
            },
            onAbout = {
                isAboutVisible = true
            },
            onLogout = {
                mainModel.logout()
                navHostController.navigate(Screen.LoginScreen.route) {
                    popUpTo(Screen.ProfileScreen.createRoute(isYou = true)) {
                        inclusive = true
                    }
                }
            },
            state = bottomSheetState,
            scope = scope
        )
    }

    if (isAboutVisible) {
        AboutPopUp {
            isAboutVisible = false
        }
    }
}