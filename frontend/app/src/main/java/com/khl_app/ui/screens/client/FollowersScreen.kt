package com.khl_app.presentation.screens

import MainViewModel
import android.app.Activity
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.khl_app.ui.navigation.Screen
import com.khl_app.ui.presentation.FollowersUiState
import com.khl_app.ui.presentation.FollowersViewModel
import com.khl_app.ui.presentation.SubscriptionState
import com.khl_app.ui.screens.client.BottomPanel
import com.khl_app.ui.screens.client.FollowerItem
import kotlinx.coroutines.launch
import com.khl_app.storage.getUserIdFromToken
import com.khl_app.ui.screens.AboutPopUp
import kotlinx.coroutines.flow.first

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

    // Получение ID пользователя для определения своего профиля
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

    // Диалог для ввода ID пользователя
    if (showSubscribeDialog) {
        AlertDialog(
            onDismissRequest = {
                showSubscribeDialog = false
                userIdInput = ""
            },
            title = { Text("Подписаться на пользователя") },
            text = {
                OutlinedTextField(
                    value = userIdInput,
                    onValueChange = { userIdInput = it },
                    label = { Text("ID пользователя") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
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
                    }
                ) {
                    Text("Подписаться")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSubscribeDialog = false
                        userIdInput = ""
                    }
                ) {
                    Text("Отмена")
                }
            }
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
                    // Кнопка добавления подписки
                    IconButton(onClick = { showSubscribeDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
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
                                    onMessageClick = {
                                        navHostController.navigate(Screen.MainScreen.createRoute(canRedact = false, isFromMenu = false, name = follower.name))
                                    },
                                    onDeleteClick = { viewModel.removeFollower(it) },
                                    onItemClick = {
                                        navHostController.navigate(Screen.ProfileScreen.createRoute(
                                            userId = follower.id,
                                            isFromMenu = false,
                                        ))
                                    },
                                    isYou = follower.id == ownUserId
                                )
                            }
                        }
                    }
                }
            }

            // Индикатор загрузки при подписке/отписке
            if (subscriptionState is SubscriptionState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x80000000)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }

    if (bottomSheetState.isVisible) {
        BottomPanel(
            onCalendar = { navHostController.navigate(Screen.MainScreen.route) },
            onTrackable = {},
            onProfile = {
                scope.launch {
                    val tokenData = mainViewModel.tokenCache.getInfo().first()
                    navHostController.navigate(Screen.ProfileScreen.createRoute(
                        userId = getUserIdFromToken(tokenData.accessToken),
                    ))
                }
            },
            onAbout = {
                isAboutVisible = true
            },
            onLogout = {
                mainModel.logout()
                navHostController.navigate(Screen.LoginScreen.route) {
                    popUpTo(Screen.ProfileScreen.route) {
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