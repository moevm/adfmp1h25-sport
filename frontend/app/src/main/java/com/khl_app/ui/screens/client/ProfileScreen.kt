package com.khl_app.ui.screens.client

import MainViewModel
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.khl_app.domain.models.FollowerResponse
import com.khl_app.domain.models.LevelSystem
import com.khl_app.ui.navigation.Screen
import com.khl_app.ui.presentation.FollowersUiState
import com.khl_app.ui.presentation.FollowersViewModel
import com.khl_app.ui.screens.AboutPopUp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextField
import androidx.compose.material3.TextButton
import androidx.compose.ui.unit.width

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    followersViewModel: FollowersViewModel,
    navHostController: NavHostController,
    userId: String? = null,
    isFromMenu: Boolean = true,
    isYou: Boolean = true
) {
    val state = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var aboutIsVisible by remember { mutableStateOf(false) }

    val followersUiState by followersViewModel.uiState.collectAsState()
    val followersList by followersViewModel.followers.collectAsState()

    var userData by remember { mutableStateOf<FollowerResponse?>(null) }

    LaunchedEffect(key1 = userId, key2 = followersList) {
        if (userId != null) {
            userData = followersList.find { it.id == userId }
        } else {
            if (followersList.isNotEmpty()) {
                userData = followersList.first()
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        followersViewModel.loadFollowers()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF2C2F3E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(
                isFromMenu = isFromMenu,
                onMenuClick = {
                    scope.launch {
                        state.show()
                    }
                },
                onBackClick = {
                    navHostController.popBackStack()
                }
            )
            when (followersUiState) {
                is FollowersUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 120.dp),
                        color = Color(0xFF6C5CE7)
                    )
                }
                is FollowersUiState.Error -> {
                    Text(
                        text = "Ошибка загрузки данных",
                        color = Color.Red,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(top = 120.dp)
                    )
                }
                is FollowersUiState.Success -> {
                    if (userData != null) {
                        ProfileContent(userData!!, isYou, followersViewModel)
                    } else {
                        Text(
                            text = "Пользователь не найден",
                            color = Color.White,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(top = 120.dp)
                        )
                    }
                }
            }
        }

        if (state.isVisible) {
            BottomPanel(
                onCalendar = {
                    viewModel.eventViewModel.setUserId("current")
                    viewModel.eventViewModel.resetAndLoadEvents()
                    navHostController.navigate(Screen.MainScreen.route)
                },
                onProfile = {},
                onAbout = {
                    aboutIsVisible = true
                },
                onLogout = {
                    viewModel.logout()
                    navHostController.navigate(Screen.LoginScreen.route) {
                        popUpTo(Screen.ProfileScreen.route) {
                            inclusive = true
                        }
                    }
                },
                onTrackable = {
                    navHostController.navigate(Screen.TrackableScreen.route)
                },
                scope = scope,
                state = state
            )
        }

        if (aboutIsVisible) {
            AboutPopUp {
                aboutIsVisible = false
            }
        }
    }
}

@Composable
fun TopBar(isFromMenu: Boolean, onMenuClick: () -> Unit, onBackClick: () -> Unit) {
    Spacer(modifier = Modifier.height(30.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 12.dp)
    ) {
        // Левая часть (кнопка меню или назад)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
        ) {
            if (isFromMenu) {
                MenuButton(onMenuClick = onMenuClick, isFromMenu = isFromMenu)
            } else {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color.White
                    )
                }
            }
        }

        // Центральная часть (Профиль)
        ProfileCenterContent(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 20.dp)
        )
    }
}

@Composable
private fun MenuButton(onMenuClick: () -> Unit, isFromMenu: Boolean) {
    IconButton(onClick = onMenuClick) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Меню",
            tint = Color.White
        )
    }
}

@Composable
fun ProfileCenterContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Профиль",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@Composable
fun ProfileContent(
    userData: FollowerResponse,
    isYou: Boolean,
    viewModel: FollowersViewModel
) {
    val points = userData.points
    val level = LevelSystem.getLevelForPoints(points)
    val nextLevelPoints = if (level.number < 4) {
        LevelSystem.levels.find { it.number == level.number + 1 }?.minPoints ?: (level.minPoints + 1000)
    } else {
        level.minPoints + 1000
    }

    val pointsForCurrentLevel = points - level.minPoints
    val totalPointsNeededForNextLevel = nextLevelPoints - level.minPoints
    val progressPercentage = if (totalPointsNeededForNextLevel > 0) {
        pointsForCurrentLevel.toFloat() / totalPointsNeededForNextLevel
    } else {
        1f
    }

    val context = LocalContext.current
    var showUploadMessage by remember { mutableStateOf(false) }
    var showCopiedMessage by remember { mutableStateOf(false) }
    var editNameDialogVisible by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(userData.name) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && isYou) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                viewModel.setAvatar(bitmap)
                showUploadMessage = true
            } catch (e: Exception) {
                Toast.makeText(context, "Ошибка загрузки изображения: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(3.dp, LevelSystem.getAvatarBorderColor(points), CircleShape)
                .background(Color.DarkGray)
                .then(
                    if (isYou) Modifier.clickable {
                        imagePickerLauncher.launch("image/*")
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (userData.avatar != null && userData.avatar.isNotEmpty()) {
                val avatarBitmap = remember(userData.avatar) {
                    try {
                        val imageBytes = Base64.decode(userData.avatar, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size).asImageBitmap()
                    } catch (e: Exception) {
                        null
                    }
                }

                if (avatarBitmap != null) {
                    Image(
                        bitmap = avatarBitmap,
                        contentDescription = "Аватар пользователя",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = userData.name.first().toString(),
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text(
                    text = userData.name.first().toString(),
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Name with edit icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = userData.name,
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.width(15.dp))

            if (isYou) {
                IconButton(
                    onClick = { editNameDialogVisible = true },
                    modifier = Modifier.size(22.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Изменить имя",
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // User ID with copy button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF3D4055))
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clickable {
                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    val clip = android.content.ClipData.newPlainText("User ID", userData.id)
                    clipboard.setPrimaryClip(clip)
                    showCopiedMessage = true
                }
        ) {
            Text(
                text = "ID: ${userData.id}",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                value = userData.points.toString(),
                label = "Баллов получено"
            )

            StatItem(
                value = userData.stats.predictedGames.toString(),
                label = "Предсказано игр"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                value = "${(userData.stats.winnerPoints.toFloat() / userData.stats.predictedGames.toFloat() * 100).toInt()}%",
                label = "Верно угадан\nПобедитель"
            )

            StatItem(
                value = "${(userData.stats.scorePoints.toFloat() / userData.stats.predictedGames.toFloat() * 100).toInt()}%",
                label = "Верность счета"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Stats Row 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                value = userData.stats.followersCount.toString(),
                label = "Отслеживают"
            )

            StatItem(
                value = userData.stats.followingCount.toString(),
                label = "Отслеживает"
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        LevelProgressBar(
            currentPoints = points,
            progressPercentage = progressPercentage
        )
    }

    if (showUploadMessage) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Snackbar(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Аватар успешно загружен",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            LaunchedEffect(key1 = showUploadMessage) {
                kotlinx.coroutines.delay(3000)
                showUploadMessage = false
            }
        }
    }

    if (showCopiedMessage) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Snackbar(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "ID скопирован в буфер обмена",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            LaunchedEffect(key1 = showCopiedMessage) {
                kotlinx.coroutines.delay(3000)
                showCopiedMessage = false
            }
        }
    }

    // Name edit dialog
    if (editNameDialogVisible) {
        AlertDialog(
            onDismissRequest = { editNameDialogVisible = false },
            title = {
                Text(
                    text = "Изменить имя",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                TextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Имя", color = Color.White.copy(alpha = 0.7f)) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.setName(newName)
                        editNameDialogVisible = false
                    }
                ) {
                    Text("Сохранить", color = Color(0xFF6C5CE7))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { editNameDialogVisible = false }
                ) {
                    Text("Отмена", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFF2C2F3E),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LevelProgressBar(
    currentPoints: Int,
    progressPercentage: Float
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Level text
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${LevelSystem.getLevelNumber(currentPoints)}",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = " уровень",
                color = Color.White,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Level milestones
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "0",
                color = Color.White,
                fontSize = 16.sp
            )

            Text(
                text = "1 ур",
                color = Color.White,
                fontSize = 16.sp
            )

            Text(
                text = "2 ур",
                color = Color.White,
                fontSize = 16.sp
            )

            Text(
                text = "3 ур",
                color = Color.White,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .padding(horizontal = 16.dp)
                .background(Color.DarkGray.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressPercentage)
                    .height(12.dp)
                    .background(Color.Green, RoundedCornerShape(6.dp))
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "0 pt",
                color = Color.White,
                fontSize = 16.sp
            )

            Text(
                text = "100 pt",
                color = Color.White,
                fontSize = 16.sp
            )

            Text(
                text = "500 pt",
                color = Color.White,
                fontSize = 16.sp
            )

            Text(
                text = "1000 pt",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}