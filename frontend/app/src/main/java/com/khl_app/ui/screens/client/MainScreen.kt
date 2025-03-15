package com.khl_app.ui.screens.client

import AuthViewModel
import MainViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khl_app.domain.models.EventPredictionItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Объединяем состояния загрузки в одну переменную для предотвращения множественных запросов
    var loadingDirection by remember { mutableStateOf(LoadDirection.NONE) }

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Сохраняем индекс отображаемого элемента перед загрузкой новых элементов
    var initialItemIndex by remember { mutableStateOf(0) }
    var initialItemOffset by remember { mutableStateOf(0) }
    var previousItemsCount by remember { mutableStateOf(0) }

    // Сохраняем текущую позицию скролла
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                if (!isLoading) {
                    initialItemIndex = index
                    initialItemOffset = offset
                    previousItemsCount = events.size
                }
            }
    }

    // Проверяем, нужно ли загрузить больше данных при достижении верха или низа списка
    LaunchedEffect(listState) {
        snapshotFlow {
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val layoutInfo = listState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount

            // Проверяем, находимся ли мы в начале списка (новые события)
            val isAtTop = firstVisibleIndex == 0 && listState.firstVisibleItemScrollOffset == 0

            // Проверяем, находимся ли мы в конце списка (старые события)
            val isAtBottom = if (totalItemsCount > 0) {
                val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                lastVisibleIndex >= totalItemsCount - 1
            } else false

            Pair(isAtTop, isAtBottom)
        }
            // Добавляем debounce — не чаще одного раза в 1000 мс
            .debounce(1000L)
            .collect { (isAtTop, isAtBottom) ->
                // Предотвращаем множественные запросы - загрузка только когда нет активной загрузки
                if (!isLoading && loadingDirection == LoadDirection.NONE && events.isNotEmpty()) {
                    if (isAtTop) {
                        // Загружаем более новые события
                        loadingDirection = LoadDirection.FUTURE
                        viewModel.loadMoreFutureEvents()
                    } else if (isAtBottom) {
                        // Загружаем более старые события
                        loadingDirection = LoadDirection.PAST
                        viewModel.loadMorePastEvents()
                    }
                }
            }
    }

    // Восстанавливаем позицию скролла после загрузки данных
    LaunchedEffect(isLoading) {
        if (!isLoading && loadingDirection != LoadDirection.NONE) {
            if (loadingDirection == LoadDirection.FUTURE && events.size > previousItemsCount) {
                // Вычисляем смещение из-за новых элементов сверху
                val newItemsCount = events.size - previousItemsCount
                // Скроллим к предыдущей позиции с учетом добавленных элементов
                listState.scrollToItem(initialItemIndex + newItemsCount, initialItemOffset)
            } else if (loadingDirection == LoadDirection.PAST) {
                // При загрузке прошлых событий восстанавливаем позицию как есть
                listState.scrollToItem(initialItemIndex, initialItemOffset)
            }

            loadingDirection = LoadDirection.NONE
        }
    }

    // Скроллим к сегодняшней дате при первой загрузке данных
    LaunchedEffect(events.isNotEmpty()) {
        if (events.isNotEmpty() && !isLoading) {
            val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yy"))
            val dateGroups = events.groupBy { it.date }.keys.toList().sortedByDescending { it }
            val todayIndex = dateGroups.indexOf(today)

            if (todayIndex >= 0) {
                // Вычисляем позицию для скролла
                var position = 0
                val groupedEvents = events.groupBy { it.date }.toList().sortedByDescending { it.first }.toMap()

                for (i in 0 until todayIndex) {
                    position += 1 + (groupedEvents[dateGroups[i]]?.size ?: 0)
                }

                listState.scrollToItem(position)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF2C2F3E)) // Темно-синий фон как на скриншоте
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopBar()

        Spacer(modifier = Modifier.height(12.dp))

        // Навигационные кнопки
        NavigationButtons(
            events = events,
            listState = listState,
            scope = scope
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading && events.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else if (error != null && events.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Ошибка: $error",
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Отображаем все события в едином хронологическом списке
            if (events.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Нет событий",
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Верхний индикатор загрузки (для будущих событий)
                    if (loadingDirection == LoadDirection.FUTURE && isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        }
                    }

                    // Основной список
                    Box(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        MatchList(
                            events = events,
                            listState = listState,
                            authViewModel = viewModel.authViewModel,
                            onPredictionMade = { viewModel.loadEvents() }  // Перезагружаем события после прогноза
                        )
                    }

                    // Нижний индикатор загрузки (для прошлых событий)
                    if (loadingDirection == LoadDirection.PAST && isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

// Перечисление для отслеживания направления загрузки
enum class LoadDirection {
    NONE, FUTURE, PAST
}

@Composable
fun NavigationButtons(
    events: List<EventPredictionItem>,
    listState: LazyListState,
    scope: CoroutineScope
) {
    // Получение сегодняшней даты
    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yy"))

    // Группируем события по дате и сортируем в обратном хронологическом порядке (новые вверху)
    val groupedEvents = events.groupBy { it.date }
        .toList()
        .sortedByDescending { it.first }
        .toMap()

    // Преобразуем в список для удобного доступа
    val dateGroups = groupedEvents.keys.toList()

    // Находим индексы важных дат
    val todayIndex = dateGroups.indexOf(today)

    // Функция для поиска предыдущей даты относительно сегодня
    fun findNearestFutureDate(): Int {
        if (dateGroups.isEmpty()) return -1
        if (todayIndex <= 0) return dateGroups.size - 1  // Если сегодня уже первый день или его нет, берем последний
        return todayIndex - 1  // Берем предыдущий день относительно сегодня
    }

    // Функция для поиска следующей даты относительно сегодня
    fun findNearestPastDate(): Int {
        if (dateGroups.isEmpty()) return -1
        if (todayIndex < 0 || todayIndex >= dateGroups.size - 1) return 0  // Если сегодня последний день или его нет, берем первый
        return todayIndex + 1  // Берем следующий день относительно сегодня
    }

    // Функция для вычисления позиции прокрутки к определенной дате
    fun calculateScrollPosition(targetDateIndex: Int): Int {
        if (targetDateIndex < 0 || dateGroups.isEmpty()) return 0

        var position = 0
        for (i in 0 until targetDateIndex) {
            position += 1 + (groupedEvents[dateGroups[i]]?.size ?: 0) // 1 за заголовок + количество событий
        }
        return position
    }

    // Определяем, какая вкладка активна
    var selectedTabIndex by remember { mutableStateOf(1) } // По умолчанию "Сегодня"

    // Цвета в соответствии со скриншотом
    val darkPurple = Color(0xFF1D1F2B) // Фон неактивных кнопок
    val lightPurple = Color(0xFF6C5CE7) // Фон активной кнопки
    val textColor = Color.White // Цвет текста

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(darkPurple),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Кнопка "Предыдущие" (скролл к ближайшим предыдущим событиям)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(4.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(if (selectedTabIndex == 0) lightPurple else darkPurple)
                .clickable {
                    selectedTabIndex = 0
                    scope.launch {
                        val pastDateIndex = findNearestPastDate()
                        if (pastDateIndex >= 0) {
                            val position = calculateScrollPosition(pastDateIndex)
                            listState.animateScrollToItem(position)
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Предыдущие",
                color = textColor,
                fontSize = 14.sp,
                fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal
            )
        }

        // Кнопка "Сегодня"
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(4.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(if (selectedTabIndex == 1) lightPurple else darkPurple)
                .clickable {
                    selectedTabIndex = 1
                    scope.launch {
                        if (dateGroups.isNotEmpty() && todayIndex >= 0) {
                            val position = calculateScrollPosition(todayIndex)
                            listState.animateScrollToItem(position)
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Сегодня",
                color = textColor,
                fontSize = 14.sp,
                fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal
            )
        }

        // Кнопка "Будущие" (скролл к ближайшим будущим событиям)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(4.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(if (selectedTabIndex == 2) lightPurple else darkPurple)
                .clickable {
                    selectedTabIndex = 2
                    scope.launch {
                        val futureDateIndex = findNearestFutureDate()
                        if (futureDateIndex >= 0) {
                            val position = calculateScrollPosition(futureDateIndex)
                            listState.animateScrollToItem(position)
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Будущие",
                color = textColor,
                fontSize = 14.sp,
                fontWeight = if (selectedTabIndex == 2) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 12.dp),
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
fun CenterContent(modifier: Modifier = Modifier) {
    val firstApiFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val date = LocalDate.now().format(firstApiFormat)

    Column(
        modifier = modifier, // Убедитесь, что фон не задается здесь
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Календарь",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White // Замените цвет текста, если нужно, под новый фон
        )
        Text(
            text = "сегодня: " + date,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f) // Замените цвет текста, если нужно
        )
    }
}

@Composable
fun MenuButton() {
    IconButton(onClick = { }) {
        Icon(
            Icons.Rounded.Menu,
            contentDescription = "Menu Button",
            modifier = Modifier.size(28.dp),
            tint = Color.White
        )
    }
}

@Composable
fun SettingsButton() {
    IconButton(onClick = { }) {
        Icon(
            Icons.Rounded.Settings,
            contentDescription = "Settings Button",
            modifier = Modifier.size(28.dp),
            tint = Color.White
        )
    }
}

@Composable
fun MatchList(
    events: List<EventPredictionItem>,
    listState: LazyListState,
    authViewModel: AuthViewModel,
    onPredictionMade: () -> Unit
) {
    // Группируем события по дате и сортируем в обратном хронологическом порядке (новые вверху)
    val groupItems = events.groupBy { it.date }
        .toList()
        .sortedByDescending { it.first } // Обратный порядок - новые вверху
        .toMap()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
    ) {
        groupItems.forEach { (date, cards) ->
            // Для каждой даты добавляем заголовок и затем события
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Divider(
                        color = Color.Gray.copy(alpha = 0.3f),
                        thickness = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )

                    // Сегодняшняя дата выделяется цветом
                    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yy"))
                    val isToday = date == today

                    Text(
                        text = date,
                        color = if (isToday) Color(0xFF6C5CE7) else Color.White.copy(alpha = 0.7f),
                        fontSize = if (isToday) 14.sp else 12.sp,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(color = Color(0xFF2C2F3E))
                            .padding(horizontal = 8.dp)
                    )
                }
            }

            items(cards) { card ->
                PredictCardItem(
                    item = card,
                    eventId = card.id.toString(),
                    authViewModel = authViewModel,
                    onPredictionMade = onPredictionMade
                )
            }
        }
    }
}