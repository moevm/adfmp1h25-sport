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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.TextStyle
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
    var showScoringInfoDialog by remember { mutableStateOf(false) }

    // Calculate points if match is over and there's both prediction and result
    val (winnerPoints, accuracyPoints) = if (item.period?.toString() == "-1" && item.prediction != null && item.result != null) {
        calculatePoints(item.prediction, item.result)
    } else {
        Pair(0, 0)
    }

    val totalPoints = winnerPoints + accuracyPoints

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
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.White
                    )
                    Text(
                        text = item.time,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                Text(
                    text = "${item.time} ${item.date}",
                    fontSize = 12.sp,
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

                // Display points for completed matches with predictions
                if (item.period?.toString() == "-1" && item.result != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Очки: $totalPoints (${winnerPoints}+${accuracyPoints})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFFFD700), // Gold color for points
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )

                        IconButton(
                            onClick = { showScoringInfoDialog = true },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Правила начисления очков",
                                tint = Color(0xFF6C5CE7)
                            )
                        }
                    }
                }
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

    if (showScoringInfoDialog) {
        ScoringInfoDialog(
            prediction = item.prediction ?: "",
            result = item.result ?: "",
            winnerPoints = winnerPoints,
            accuracyPoints = accuracyPoints,
            onDismiss = { showScoringInfoDialog = false }
        )
    }
}

/**
 * Calculate points based on the prediction and actual result
 * Returns a Pair of (winnerPoints, accuracyPoints)
 */
fun calculatePoints(prediction: String, actual: String): Pair<Int, Int> {
    val predParts = prediction.split(":")
    val actualParts = actual.split(":")

    // Ensure we have valid numeric values
    val predHome = predParts.getOrNull(0)?.toIntOrNull() ?: 0
    val predAway = predParts.getOrNull(1)?.toIntOrNull() ?: 0
    val actualHome = actualParts.getOrNull(0)?.toIntOrNull() ?: 0
    val actualAway = actualParts.getOrNull(1)?.toIntOrNull() ?: 0

    // Determine winners
    val predWinner = when {
        predHome > predAway -> 1
        predHome < predAway -> 0
        else -> -1 // Draw
    }

    val actualWinner = when {
        actualHome > actualAway -> 1
        actualHome < actualAway -> 0
        else -> -1 // Draw
    }

    // Points for guessing the winner
    val winnerPoints = if (predWinner == actualWinner) 1 else 0

    // Points for score accuracy
    val scoreAccuracy = when {
        predHome == actualHome && predAway == actualAway -> 3 // Exact match
        (Math.abs(predHome - actualHome) + Math.abs(predAway - actualAway)) == 1 -> 2 // Off by 1
        (Math.abs(predHome - actualHome) + Math.abs(predAway - actualAway)) == 2 -> 1 // Off by 2
        else -> 0 // Off by more than 2
    }

    return Pair(winnerPoints, scoreAccuracy)
}

@Composable
fun ScoringInfoDialog(
    prediction: String,
    result: String,
    winnerPoints: Int,
    accuracyPoints: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Правила начисления очков", style = TextStyle(color = Color(0xFF6C5CE7), fontSize = 20.sp)) },
        text = {
            Column {
                Text("За угаданный исход матча: 1 очко")
                Text("За точный счет: 3 очка")
                Text("За ошибку в 1 гол: 2 очка")
                Text("За ошибку в 2 гола: 1 очко")
                Text("За ошибку более чем в 2 гола: 0 очков")
                Text("Формула ошибки: |Gh-Ghp| + |Gg-Ggp|")
                Text("Где Gh и Ghp - голы домашней команды правильные и предсказанные")
                Text("Gg и Ggp - голы гостевой команды правильные и предсказанные")

                Text(
                    text = "Ваш прогноз: $prediction",
                    modifier = Modifier.padding(top = 16.dp),
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
                Text("Итоговый счет: $result", style = TextStyle(fontWeight = FontWeight.Bold))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2D2D3A)
                    )
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Вы получили:",
                            fontWeight = FontWeight.Bold
                        )
                        Text("За исход матча: $winnerPoints очко")
                        Text("За точность счета: $accuracyPoints очка")
                        Text(
                            text = "Всего: ${winnerPoints + accuracyPoints} очков",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Закрыть", style = TextStyle(color = Color.White))
            }
        }
    )
}