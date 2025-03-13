package com.khl_app.ui.screens.client

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.khl_app.domain.models.EventPredictionItem
import com.khl_app.domain.models.Team
import com.khl_app.ui.themes.KhlAppTheme

//@Composable
//fun PredictCard() {
//    Column {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 8.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            HorizontalDivider(
//                color = Color.Gray,
//                thickness = 1.dp,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .align(Alignment.Center)
//            )
//
//            Text(
//                text = "24.02.2025",
//                color = Color.Black,
//                fontSize = 12.sp,
//                textAlign = TextAlign.Center,
//                modifier = Modifier
//                    .background(Color.White)
//                    .padding(horizontal = 8.dp)
//            )
//        }
//        PredictCardItem()
//    }
//}

@Composable
fun PredictCardItem(item: EventPredictionItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(vertical = 2.dp)
            .background(color = MaterialTheme.colorScheme.primary)
            .wrapContentHeight()
            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item.teamA.name
            )
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
                modifier = Modifier.size(32.dp)
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (item.prediction != null || item.result != null) {
                Text(
                    text = "${item.time} ${item.date}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    if (item.prediction != null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {

                            Text(
                                text = item.prediction,
                                fontSize = 20.sp,
                            )
                            Text(
                                fontSize = 12.sp,
                                text = "Prediction"
                            )
                        }
                    }
                    if (item.result != null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Text(
                                text = item.result,
                                fontSize = 20.sp,
                            )
                            Text(
                                fontSize = 12.sp,
                                text = "Result"
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "${item.time} today",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item.teamB.name
            )
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
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardPreview() {
    val logo =
        "https://www.google.com/url?sa=i&url=https%3A%2F%2Fru.wikipedia.org%2Fwiki%2F%25D0%259A%25D0%25BE%25D0%25BD%25D1%2582%25D0%25B8%25D0%25BD%25D0%25B5%25D0%25BD%25D1%2582%25D0%25B0%25D0%25BB%25D1%258C%25D0%25BD%25D0%25B0%25D1%258F_%25D1%2585%25D0%25BE%25D0%25BA%25D0%25BA%25D0%25B5%25D0%25B9%25D0%25BD%25D0%25B0%25D1%258F_%25D0%25BB%25D0%25B8%25D0%25B3%25D0%25B0&psig=AOvVaw3QY7-Ugq1-vOP6aEvVVZAJ&ust=1741881808441000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCODT5cX1hIwDFQAAAAAdAAAAABAE"
    KhlAppTheme {
        PredictCardItem(
            EventPredictionItem(
                teamA = Team(logo, "teamA"),
                teamB = Team(logo, "teamB"),
                date = "24.02.2024",
                time = "17:32",
                prediction = null,
                result = "2:11"
            )
        )
    }
}
