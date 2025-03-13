package com.khl_app.ui.screens.client

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.khl_app.domain.models.EventPredictionItem
import com.khl_app.domain.models.Team
import com.khl_app.ui.themes.KhlAppTheme
import com.khl_app.ui.view_models.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun MainScreen(viewModel: MainViewModel) {
    val logo =
        "https://www.google.com/url?sa=i&url=https%3A%2F%2Fru.wikipedia.org%2Fwiki%2F%25D0%259A%25D0%25BE%25D0%25BD%25D1%2582%25D0%25B8%25D0%25BD%25D0%25B5%25D0%25BD%25D1%2582%25D0%25B0%25D0%25BB%25D1%258C%25D0%25BD%25D0%25B0%25D1%258F_%25D1%2585%25D0%25BE%25D0%25BA%25D0%25BA%25D0%25B5%25D0%25B9%25D0%25BD%25D0%25B0%25D1%258F_%25D0%25BB%25D0%25B8%25D0%25B3%25D0%25B0&psig=AOvVaw3QY7-Ugq1-vOP6aEvVVZAJ&ust=1741881808441000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCODT5cX1hIwDFQAAAAAdAAAAABAE"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopBar()
        Spacer(modifier = Modifier.height(20.dp))
        MatchList(listOf(
            EventPredictionItem(
                teamA = Team(logo, "teamA"),
                teamB = Team(logo, "teamB"),
                date = "25.02.2024",
                time = "17:32",
                prediction = "2:1",
                result = "2:11"
            ),
            EventPredictionItem(
                teamA = Team(logo, "teamA"),
                teamB = Team(logo, "teamB"),
                date = "24.02.2024",
                time = "17:32",
                prediction = null,
                result = "2:11"
            ),
            EventPredictionItem(
                teamA = Team(logo, "teamA"),
                teamB = Team(logo, "teamB"),
                date = "24.02.2024",
                time = "17:32",
                prediction = null,
                result = "2:11"
            )
        ))
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MenuButton()
        CenterContent(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
        )
        SettingsButton()
    }
}

@Composable
fun CenterContent(modifier: Modifier) {
    val firstApiFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val date = LocalDate.now().format(firstApiFormat)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Календарь",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.surface
        )
        Text(
            text = "сегодня: " + date.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun MenuButton() {
    IconButton(onClick = { }) {
        Icon(
            Icons.Rounded.Menu,
            contentDescription = "Menu Button",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun SettingsButton() {
    IconButton(onClick = {

    }) {
        Icon(
            Icons.Rounded.Settings,
            contentDescription = "Settings Button",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun MatchList(events: List<EventPredictionItem>) {
    var lastDate by remember { mutableStateOf("") }
    val groupItems = events.groupBy { it.date }
    LazyColumn {
        groupItems.forEach { (date, cards) ->
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.surface,
                        thickness = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )

                    Text(
                        text = date,
                        color = MaterialTheme.colorScheme.surface,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.background)
                            .padding(horizontal = 8.dp)
                    )
                }
            }
            items(cards) { card ->
                PredictCardItem(card)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    KhlAppTheme {
        MainScreen(viewModel())
    }
}