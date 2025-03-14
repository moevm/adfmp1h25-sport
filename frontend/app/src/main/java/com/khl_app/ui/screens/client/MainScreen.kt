package com.khl_app.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khl_app.domain.models.EventPredictionItem
import com.khl_app.ui.view_models.MainViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Отслеживаем выбранную вкладку
    var selectedTabIndex by remember { mutableStateOf(1) } // 0=Предыдущие, 1=Сегодня, 2=Будущие

    // Проверяем, нужно ли загрузить больше данных при достижении конца списка
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { firstVisibleItem ->
                if (firstVisibleItem == 0 && !isLoading) {
                    // Мы в начале списка, загружаем более ранние события
                    viewModel.loadMorePastEvents()
                }
            }
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 1
        }.collect { isAtEnd ->
            if (isAtEnd && !isLoading && events.isNotEmpty()) {
                // Мы в конце списка, загружаем более поздние события
                viewModel.loadMoreFutureEvents()
            }
        }
    }

    // Загружаем события при первом отображении экрана
    LaunchedEffect(Unit) {
        if (events.isEmpty()) {
            viewModel.loadEvents()
        }
    }

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

        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Предыдущие") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Сегодня") }
            )
            Tab(
                selected = selectedTabIndex == 2,
                onClick = { selectedTabIndex = 2 },
                text = { Text("Будущие") }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (isLoading && events.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null && events.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Ошибка: $error",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Фильтруем события в зависимости от выбранной вкладки
            val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yy"))
            val filteredEvents = when (selectedTabIndex) {
                0 -> events.filter { it.date < today } // Предыдущие события
                1 -> events.filter { it.date == today } // Сегодняшние события
                2 -> events.filter { it.date > today } // Будущие события
                else -> events
            }

            if (filteredEvents.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Нет событий",
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                MatchList(events = filteredEvents, listState = listState)
            }

            // Показываем индикатор загрузки внизу при загрузке дополнительных данных
            if (isLoading && events.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }
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
    IconButton(onClick = { }) {
        Icon(
            Icons.Rounded.Settings,
            contentDescription = "Settings Button",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun MatchList(events: List<EventPredictionItem>, listState: LazyListState) {
    val groupItems = events.groupBy { it.date }

    LazyColumn(state = listState) {
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