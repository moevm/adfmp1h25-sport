package com.khl_app.ui.screens.client

import AuthViewModel
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.khl_app.domain.models.EventPredictionItem

@Composable
fun PredictCardItem(item: EventPredictionItem, eventId: String, authViewModel: AuthViewModel, onPredictionMade: () -> Unit, canRedact: Boolean) {
    val context = LocalContext.current
    var showPredictDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable {
                if (!canRedact) {
                    Toast.makeText(context, "Не ваш календарь", Toast.LENGTH_SHORT).show()
                } else if (item.period == null) {
                    showPredictDialog = true
                } else {
                    Toast.makeText(context, "Матч уже начался", Toast.LENGTH_SHORT).show()
                }
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = item.teamA.name,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Box(
                modifier = Modifier
                    .size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.teamA.logo)
                            .crossfade(true)
                            .transformations(CircleCropTransformation())
                            .build(),
                    ),
                    contentDescription = item.teamA.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1.5f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (item.period == null) {
                Column {
                    Text(
                        text = item.date,
                        fontSize = 12.sp, // Размер шрифта для даты
                        fontWeight = FontWeight.Light,
                        color = Color.White
                    )
                    Text(
                        text = item.time,
                        fontSize = 20.sp, // Крупнее
                        fontWeight = FontWeight.Bold, // Более жирный
                        color = Color.White
                    )
                }
            } else {
                Text(
                    text = "${item.time} ${item.date}",
                    fontSize = 12.sp, // Меньше
                    fontWeight = FontWeight.Light,
                    color = Color.White
                )
            }

            if (item.result != null && item.period != null) {
                Text(
                    text = item.result,
                    fontSize = if (item.prediction == null) 28.sp else 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            Log.d("game", item.prediction.toString())
            if (item.prediction != null) {
                val predictionColor = when {
                    item.period?.toString() == "-1" && item.result != null -> {
                        val predictionParts = item.prediction.split(":")
                        val resultParts = item.result.split(":")

                        val predictedWinner = when {
                            predictionParts[0].toIntOrNull() ?: 0 > predictionParts[1].toIntOrNull() ?: 0 -> "A"
                            predictionParts[0].toIntOrNull() ?: 0 < predictionParts[1].toIntOrNull() ?: 0 -> "B"
                            else -> "DRAW"
                        }

                        val actualWinner = when {
                            resultParts[0].toIntOrNull() ?: 0 > resultParts[1].toIntOrNull() ?: 0 -> "A"
                            resultParts[0].toIntOrNull() ?: 0 < resultParts[1].toIntOrNull() ?: 0 -> "B"
                            else -> "DRAW"
                        }

                        if (predictedWinner == actualWinner) Color.Green else Color.Red
                    }
                    item.period == null -> Color.White
                    else -> Color(0xFF6C5CE7)
                }

                Text(
                    text = "Прогноз: ${item.prediction}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = predictionColor,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            if (item.period != null && item.period.toString() != "-1") {
                if(item.period.toString() == "10") {
                    Text(
                        text = "Период: ${item.period}",
                        fontSize = 14.sp,
                        color = Color(0xFF6C5CE7),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
                else{
                    Text(
                        text = "Перерыв",
                        fontSize = 14.sp,
                        color = Color(0xFF6C5CE7),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }

            if (item.result == null && item.prediction == null && item.period == null) {
                Text(
                    text = "Нажмите, чтобы сделать прогноз",
                    fontSize = 12.sp,
                    color = Color(0xFF6C5CE7)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = item.teamB.name,
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.End),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.teamB.logo)
                            .crossfade(true)
                            .transformations(CircleCropTransformation())
                            .build(),
                    ),
                    contentDescription = item.teamB.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(60.dp)
                )
            }
        }
    }

    if (showPredictDialog) {
        PredictDialog(
            item = item,
            onDismiss = { showPredictDialog = false },
            authViewModel = authViewModel,
            eventId = eventId,
            onPredictionMade = onPredictionMade
        )
    }
}