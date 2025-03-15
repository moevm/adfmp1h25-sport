package com.khl_app.ui.screens.client

import MainViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

var ProfilePicture = ""
var ProfileName = "John Doe"

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF2C2F3E))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //ProfileTopBar(onMenuClick = { navHostController.navigate(Screen.MainScreen.route) })

        Spacer(modifier = Modifier.height(16.dp))

        // Фото профиля
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(Color.DarkGray.copy(alpha = 0.3f))
                .clickable { showImageDialog = true }, // Открываем диалог при нажатии
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
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(120.dp)
                )
            } else {
                Text(
                    text = "Add Photo",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        // Диалог для ввода URL
        if (showImageDialog) {
            AlertDialog(
                onDismissRequest = { showImageDialog = false },
                title = { Text("Insert Image URL", color = Color.White) },
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
                            tempUrl = "" // Очищаем временное поле
                            showImageDialog = false // Закрываем диалог
                        }
                    ) {
                        Text("OK", color = Color(0xFF6C5CE7))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            tempUrl = "" // Очищаем временное поле
                            showImageDialog = false // Закрываем диалог
                        }
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                },
                containerColor = Color(0xFF2C2F3E), // Цвет фона диалога
                textContentColor = Color.White // Цвет текста внутри диалога
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Кнопка для смены фото (доступна только в редактируемом режиме)
        if (isEditable) {
            Button(
                onClick = { showImageDialog = true }, // Открываем диалог вместо прямой логики
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C5CE7))
            ) {
                Text("Change Profile Picture", color = Color.White)
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
                .background(Color(0xFFBB86FC), RoundedCornerShape(8.dp))
                .padding(8.dp)
        )

        // Диалог для ввода имени
        if (showNameDialog) {
            AlertDialog(
                onDismissRequest = { showNameDialog = false },
                title = { Text("Edit Profile Name", color = Color.White) },
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
                            if (tempName.isNotBlank()) { // Проверка на непустое значение
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
                        Text("Cancel", color = Color.White)
                    }
                },
                containerColor = Color(0xFF2C2F3E),
                textContentColor = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        }
    }


//@Composable
//fun ProfileTopBar(onMenuClick: () -> Unit) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 10.dp, vertical = 12.dp),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        MenuButton(onMenuClick = onMenuClick)
//        ProfileCenterContent(
//            modifier = Modifier
//                .weight(1f)
//                .padding(horizontal = 20.dp)
//        )
//        SettingsButton()
//    }
//}

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

