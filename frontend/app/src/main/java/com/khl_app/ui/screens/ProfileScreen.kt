package com.khl_app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import MainViewModel

@Composable
fun ProfileScreen(viewModel: MainViewModel) {
    var profileName by remember { mutableStateOf("John Doe") }
    var profilePicture by remember { mutableStateOf<ImageBitmap?>(null) }
    var textField1 by remember { mutableStateOf("") }
    var textField2 by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        profilePicture?.let {
            Image(bitmap = it, contentDescription = null)
        }

        Button(onClick = { /* Change profile picture logic */ }) {
            Text("Change Profile Picture")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Profile Name", fontSize = 20.sp)
        BasicTextField(value = profileName, onValueChange = { profileName = it })

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Field 1", fontSize = 20.sp)
        BasicTextField(value = textField1, onValueChange = { textField1 = it })

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Field 2", fontSize = 20.sp)
        BasicTextField(value = textField2, onValueChange = { textField2 = it })
    }
}