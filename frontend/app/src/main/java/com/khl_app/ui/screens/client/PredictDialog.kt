package com.khl_app.ui.screens.client

import AuthViewModel
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.khl_app.domain.ApiClient
import com.khl_app.domain.models.EventPredictionItem
import kotlinx.coroutines.launch

@Composable
fun PredictDialog(
    item: EventPredictionItem,
    onDismiss: () -> Unit,
    authViewModel: AuthViewModel,
    eventId: String,
    onPredictionMade: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var scoreA by remember { mutableStateOf("") }
    var scoreB by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (item.period != null) {
            Toast.makeText(context, "Матч уже начался", Toast.LENGTH_SHORT).show()
            onDismiss()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2C2F3E)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TeamScoreInput(
                        team = item.teamA,
                        score = scoreA,
                        onScoreChange = { scoreA = it }
                    )

                    Text(
                        text = ":",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    TeamScoreInput(
                        team = item.teamB,
                        score = scoreB,
                        onScoreChange = { scoreB = it }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (error != null) {
                    Text(
                        text = error!!,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        if (scoreA.isNotEmpty() && scoreB.isNotEmpty()) {
                            try {
                                val scoreAInt = scoreA.toInt()
                                val scoreBInt = scoreB.toInt()

                                if (scoreAInt < 0 || scoreBInt < 0) {
                                    error = "Счёт не может быть отрицательным"
                                    return@Button
                                }

                                val finalScore = "$scoreA:$scoreB"
                                isLoading = true
                                error = null

                                scope.launch {
                                    try {
                                        val token = authViewModel.getAuthToken()
                                        val response = ApiClient.apiService.postPredict(
                                            token = token,
                                            userId = "current",
                                            score = finalScore,
                                            eventId = eventId
                                        )

                                        isLoading = false

                                        if (response.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                "Прогноз успешно сохранен",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onPredictionMade()
                                            onDismiss()
                                        } else {
                                            error = "Ошибка: ${response.message()}"
                                        }
                                    } catch (e: Exception) {
                                        isLoading = false
                                        error = "Ошибка: ${e.message}"
                                    }
                                }
                            } catch (e: NumberFormatException) {
                                error = "Некорректный формат счёта"
                            }
                        } else {
                            error = "Введите счёт обеих команд"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6C5CE7)
                    ),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Сохранить",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TeamScoreInput(
    team: com.khl_app.domain.models.Team,
    score: String,
    onScoreChange: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(120.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(team.logo)
                        .crossfade(true)
                        .transformations(CircleCropTransformation())
                        .build(),
                ),
                contentDescription = team.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = team.name,
            color = Color.White,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = score,
            onValueChange = {
                if (it.length <= 2 && (it.isEmpty() || it.all { char -> char.isDigit() })) {
                    onScoreChange(it)
                }
            },
            modifier = Modifier
                .width(70.dp)
                .height(56.dp),
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
    }
}