package com.khl_app.ui.screens.client

import MainViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.khl_app.ui.screens.AboutPopUp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.khl_app.ui.navigation.Screen
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

var ProfilePicture = ""
var ProfileName = "John Doe"
var Level = 1
var Score = 1
var Forecasts = 1
var Following = 1
var Followers = 1
var WinnerPoints = 1
var ScorePoints = 1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    isEditable: Boolean = false
) {
    var showImageDialog by remember { mutableStateOf(false) } // Состояние для отображения диалога
    var showNameDialog by remember { mutableStateOf(false) }
    var tempUrl by remember { mutableStateOf("") } // Временное хранилище для URL в диалоге
    var tempName by remember { mutableStateOf("") }
    val state = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var aboutIsVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF2C2F3E))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileTopBar(onMenuClick = {
            scope.launch {
                state.show()
            }
        })

        Spacer(modifier = Modifier.height(30.dp))

        // Фото профиля
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(Color.DarkGray.copy(alpha = 0.3f))
                .clickable { showImageDialog = true },
            contentAlignment = Alignment.Center
        ) {
            if (ProfilePicture != "") {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(ProfilePicture)
                            .crossfade(true)
                            .transformations(CircleCropTransformation())
                            .build()
                    ),
                    contentDescription = "Фото профиля",
                    modifier = Modifier.size(120.dp)
                )
            } else {
                Text(
                    text = "Добавить фото",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        // Диалог для ввода URL
        if (showImageDialog) {
            AlertDialog(
                onDismissRequest = { showImageDialog = false },
                title = { Text("Вставьте ссылку...", color = Color.White) },
                text = {
                    BasicTextField(
                        value = tempUrl,
                        onValueChange = { tempUrl = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1D1F2B), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 16.sp)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            ProfilePicture = tempUrl
                            tempUrl = ""
                            showImageDialog = false
                        }
                    ) {
                        Text("OK", color = Color(0xFF6C5CE7))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            tempUrl = ""
                            showImageDialog = false
                        }
                    ) {
                        Text("Отмена", color = Color.White)
                    }
                },
                containerColor = Color(0xFF2C2F3E),
                textContentColor = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Кнопка для смены фото (доступна только в редактируемом режиме)
        if (isEditable) {
            Button(
                onClick = { showImageDialog = true },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C5CE7))
            ) {
                Text("Изменить фото профиля", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кликабельное имя
        Text(
            text = ProfileName,
            color = Color.White,
            fontSize = 48.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clickable { showNameDialog = true }
                .background(Color(0xFF6C5CE7), RoundedCornerShape(8.dp))
                .padding(8.dp)
        )

        // Диалог для ввода имени
        if (showNameDialog) {
            AlertDialog(
                onDismissRequest = { showNameDialog = false },
                title = { Text("Изменить имя профиля", color = Color.White) },
                text = {
                    BasicTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1D1F2B), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 16.sp)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (tempName.isNotBlank()) {
                                ProfileName = tempName
                            }
                            tempName = ""
                            showNameDialog = false
                        }
                    ) {
                        Text("OK", color = Color(0xFF6C5CE7))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            tempName = ""
                            showNameDialog = false
                        }
                    ) {
                        Text("Отмена", color = Color.White)
                    }
                },
                containerColor = Color(0xFF2C2F3E),
                textContentColor = Color.White
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Прокручиваемая часть с текстовыми полями
        LazyColumn(
            modifier = Modifier
                .weight(1f) // Занимает оставшееся пространство
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = Level.toString(),
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "Уровень",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(4.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Text(
                    text = Score.toString(),
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "Баллов получено",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(4.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Text(
                    text = Forecasts.toString(),
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "Предсказано игр",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(4.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Text(
                    text = "${(ScorePoints.toFloat() / Forecasts * 100).toInt()}%",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "Верно предсказан счет",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(4.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Text(
                    text = "${(WinnerPoints.toFloat() / Forecasts * 100).toInt()}%",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "Верно предсказан победитель",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(4.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Text(
                    text = Followers.toString(),
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "Подписчики",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(4.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Text(
                    text = Following.toString(),
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "Подписки",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(4.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        
        if (state.isVisible) {
            BottomPanel(
                onCalendar = {
                    navHostController.navigate(Screen.MainScreen.route) {
                        popUpTo(Screen.ProfileScreen.route) {
                            inclusive = true
                        }
                    }
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
fun ProfileTopBar(onMenuClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MenuButton(onMenuClick = onMenuClick)
        ProfileCenterContent(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
        )
    }
}

@Composable
fun ProfileCenterContent(modifier: Modifier = Modifier) {
    val firstApiFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val date = LocalDate.now().format(firstApiFormat)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Профиль",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        Text(
            text = "сегодня: $date",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}
