package com.khl_app.ui.screens.auth

import MainViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khl_app.ui.screens.AboutPopUp

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onLogin: () -> Unit,
    onRegistration: () -> Unit,
) {
    val defaultErrorMsg: String? = null
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf(defaultErrorMsg) }
    var isAboutVisible by remember { mutableStateOf(false) }


    val darkGrayBackground = Color(0xFF333333)
    val lightGrayText = Color(0xFFCCCCCC)
    val lightGrayButton = Color(0xFF666666)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(darkGrayBackground)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 0.dp),
                    value = login,
                    onValueChange = {
                        login = it
                    },
                    placeholder = {
                        Text(
                            text = "Логин",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = lightGrayText
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Login Icon",
                            tint = lightGrayText
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = lightGrayText
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = darkGrayBackground,
                        focusedContainerColor = darkGrayBackground,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(lightGrayText)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.padding(top = 5.dp)
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 0.dp),
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    placeholder = {
                        Text(
                            text = "Пароль",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = lightGrayText
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password Icon",
                            tint = lightGrayText
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = lightGrayText
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = darkGrayBackground,
                        focusedContainerColor = darkGrayBackground,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(lightGrayText)
                )
            }

            Box(modifier = Modifier.padding(top = 10.dp))

            Button(
                onClick = {
                    viewModel.register(login, password) { success ->
                        if (success.isNullOrEmpty()) {
                            onRegistration()
                        } else {
                            errorMessage = success
                        }
                    }
                },
                modifier = Modifier.width(150.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = lightGrayButton,
                    contentColor = lightGrayText
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Регистрация",
                    fontSize = 16.sp
                )
            }

            TextButton(
                onClick = onLogin,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "Войти",
                    color = lightGrayText,
                    fontSize = 14.sp
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Мы не относимся к КХЛ и не несем ответственности за содержание",
                    fontSize = 12.sp,
                    color = lightGrayText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.border(width = 1.dp, shape = RoundedCornerShape(5.dp), color = lightGrayButton)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "About app",
                    modifier = Modifier.clickable {
                        isAboutVisible = true
                    },
                    color = lightGrayText,
                    fontSize = 12.sp,
                    textDecoration = TextDecoration.Underline,
                )
            }

        }
        if (isAboutVisible) {
            AboutPopUp {
                isAboutVisible = false
            }
        }
    }
}