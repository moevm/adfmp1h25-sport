package com.khl_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.khl_app.ui.themes.KhlAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPopUp(onDismiss: () -> Unit) {

    BasicAlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        modifier = Modifier
            .background(color = Color.DarkGray)
            .height(100.dp),
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true
        ),
    ) {
        Column(
            modifier = Modifier.wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = "О приложении",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = "Приложение предназначено для прогнозирования матчей и отслеживание статистики прогнозов пользователя и его друзей, выполнли Кузнецов Н., Чубан Д. гр 1303, Егор Б. гр 1304",
                color = Color.White,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 10.dp),
                textAlign = TextAlign.Justify
            )
        }
    }

}

@Preview
@Composable
fun AboutPreview() {
    KhlAppTheme {
        AboutPopUp({})
    }
}