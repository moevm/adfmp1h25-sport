package com.khl_app.ui.screens.client

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun PredictCardItem(item: EventPredictionItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Team A column (left)
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

        // Center column (scores and time)
        // Center column (scores and time)
        Column(
            modifier = Modifier.weight(1.5f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${item.time} ${item.date}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                color = Color.White
            )

            // Display result if available
            if (item.result != null) {
                Text(
                    text = "Итог: ${item.result}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            // Display prediction if available
            Log.d("game", item.prediction.toString())
            if (item.prediction != null) {
                Text(
                    text = "Прогноз: ${item.prediction}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = when {
                        item.result == null -> Color.White // No result yet
                        item.prediction == item.result -> Color.Green // Correct prediction
                        else -> Color.Red // Incorrect prediction
                    },
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            // If neither prediction nor result is available
            if (item.result == null && item.prediction == null) {
                Text(
                    text = "Сегодня",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // Team B column (right)
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
}