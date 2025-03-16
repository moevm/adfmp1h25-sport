package com.khl_app.ui.screens.client

import AuthViewModel
import MainViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.khl_app.domain.models.EventPredictionItem
import com.khl_app.storage.getUserIdFromToken
import com.khl_app.ui.navigation.Screen
import com.khl_app.ui.screens.AboutPopUp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    isFromMenu: Boolean = true,
    name: String? = null,
    canRedact: Boolean = true
) {
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var loadingDirection by remember { mutableStateOf(LoadDirection.NONE) }
    val bottomSheetState = rememberModalBottomSheetState()
    var aboutState by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()


    var initialItemIndex by remember { mutableStateOf(0) }
    var initialItemOffset by remember { mutableStateOf(0) }
    var previousItemsCount by remember { mutableStateOf(0) }

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

    LaunchedEffect(listState) {
        snapshotFlow {
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val layoutInfo = listState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount

            val isAtTop = firstVisibleIndex == 0 && listState.firstVisibleItemScrollOffset == 0

            val isAtBottom = if (totalItemsCount > 0) {
                val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                lastVisibleIndex >= totalItemsCount - 1
            } else false

            Pair(isAtTop, isAtBottom)
        }
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

    LaunchedEffect(isLoading) {
        if (!isLoading && loadingDirection != LoadDirection.NONE) {
            if (loadingDirection == LoadDirection.FUTURE && events.size > previousItemsCount) {
                val newItemsCount = events.size - previousItemsCount
                listState.scrollToItem(initialItemIndex + newItemsCount, initialItemOffset)
            } else if (loadingDirection == LoadDirection.PAST) {
                listState.scrollToItem(initialItemIndex, initialItemOffset)
            }

            loadingDirection = LoadDirection.NONE
        }
    }

    LaunchedEffect(events.isNotEmpty()) {
        if (events.isNotEmpty() && !isLoading) {
            val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yy"))
            val dateGroups = events.groupBy { it.date }.keys.toList().sortedByDescending { it }
            val todayIndex = dateGroups.indexOf(today)

            if (todayIndex >= 0) {
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
            .padding(top = 20.dp, bottom = 10.dp, start = 10.dp, end = 10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopBar(
            viewModel = viewModel,
            onMenuClick = {
                if(isFromMenu) {
                    scope.launch {
                        bottomSheetState.show()
                    }
                } else {
                    navHostController.navigate(Screen.TrackableScreen.route)
                }
            },
            onFilterApplied = {
                scope.launch {
                    loadingDirection = LoadDirection.NONE
                    viewModel.loadEvents()
                }
            },
            name = name,
            isFromMenu = isFromMenu
        )

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

                    Box(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        MatchList(
                            events = events,
                            listState = listState,
                            authViewModel = viewModel.authViewModel,
                            onPredictionMade = { viewModel.loadEvents() },
                            canRedact = canRedact
                        )
                    }

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
        if (bottomSheetState.isVisible) {
            BottomPanel(
                onCalendar = {},
                onTrackable = {
                    navHostController.navigate(Screen.TrackableScreen.route)
                },
                onProfile = {
                    scope.launch {
                        val tokenData = viewModel.tokenCache.getInfo().first() // Use first() to get single value
                        navHostController.navigate(Screen.ProfileScreen.createRoute(
                            userId = getUserIdFromToken(tokenData.accessToken), isYou = true// Using the imported function to get ID
                        ))
                    }
                },
                onAbout = {
                    aboutState = true
                },
                onLogout = {
                    viewModel.logout()
                    navHostController.navigate(Screen.LoginScreen.route) {
                        popUpTo(Screen.MainScreen.route) {
                            inclusive = true
                        }
                    }
                },
                bottomSheetState,
                scope
            )
        }
        if (aboutState) {
            AboutPopUp {
                aboutState = false
            }
        }
    }
}

enum class LoadDirection {
    NONE, FUTURE, PAST
}

@Composable
fun NavigationButtons(
    events: List<EventPredictionItem>,
    listState: LazyListState,
    scope: CoroutineScope
) {
    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yy"))

    val groupedEvents = events.groupBy { it.date }
        .toList()
        .sortedByDescending { it.first }
        .toMap()

    val dateGroups = groupedEvents.keys.toList()

    val todayIndex = dateGroups.indexOf(today)

    fun findNearestFutureDate(): Int {
        if (dateGroups.isEmpty()) return -1
        if (todayIndex <= 0) return dateGroups.size - 1
        return todayIndex - 1
    }

    fun findNearestPastDate(): Int {
        if (dateGroups.isEmpty()) return -1
        if (todayIndex < 0 || todayIndex >= dateGroups.size - 1) return 0
        return todayIndex + 1
    }

    fun calculateScrollPosition(targetDateIndex: Int): Int {
        if (targetDateIndex < 0 || dateGroups.isEmpty()) return 0

        var position = 0
        for (i in 0 until targetDateIndex) {
            position += 1 + (groupedEvents[dateGroups[i]]?.size ?: 0)
        }
        return position
    }

    var selectedTabIndex by remember { mutableStateOf(1) }

    val darkPurple = Color(0xFF1D1F2B)
    val lightPurple = Color(0xFF6C5CE7)
    val textColor = Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(darkPurple),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
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
fun TopBar(
    viewModel: MainViewModel,
    onMenuClick: () -> Unit,
    onFilterApplied: () -> Unit,
    name: String?,
    isFromMenu: Boolean
) {
    Spacer(modifier = Modifier.height(30.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MenuButton(isFromMenu = isFromMenu, onMenuClick = onMenuClick)
        CenterContent(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            name = name
        )
        SettingsButton(
            mainViewModel = viewModel,
            onFilterApplied = onFilterApplied
        )
    }
}

@Composable
fun CenterContent(modifier: Modifier = Modifier, name: String?) {
    val firstApiFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val date = LocalDate.now().format(firstApiFormat)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (name != null && name != "{name}") "Календарь $name" else "Календарь",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        Text(
            text = "сегодня: " + date,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun MenuButton(isFromMenu: Boolean, onMenuClick: () -> Unit) {
    IconButton(onClick = onMenuClick) {
        if (isFromMenu) {
            Icon(
                Icons.Rounded.Menu,
                contentDescription = "Menu Button",
                modifier = Modifier.size(28.dp),
                tint = Color.White
            )
        } else {
            Icon(
                Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back Button",
                modifier = Modifier.size(28.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun SettingsButton(
    mainViewModel: MainViewModel,
    onFilterApplied: () -> Unit
) {
    val showFilterDialog = remember { mutableStateOf(false) }

    val selectedDate = remember { mutableStateOf<LocalDate?>(null) }
    val selectedTeamIds = remember { mutableStateOf<List<String>>(emptyList()) }

    val teamsMap by mainViewModel.teamViewModel.teamsMap.collectAsState()

    val teamsForFilter = teamsMap.values.map { teamData ->
        TeamBasicInfo(
            id = teamData.id.toString(),
            name = teamData.name,
            logoUrl = teamData.image,
            division = teamData.division
        )
    }

    IconButton(onClick = { showFilterDialog.value = true }) {
        Icon(
            Icons.Rounded.Settings,
            contentDescription = "Настройки и фильтры",
            modifier = Modifier.size(28.dp),
            tint = Color.White
        )
    }

    FilterDialog(
        show = showFilterDialog.value,
        teams = teamsForFilter,
        initialSelectedDate = selectedDate.value,
        initialSelectedTeams = selectedTeamIds.value,
        onDismiss = { showFilterDialog.value = false },
        onApply = { date, teamIds ->
            selectedDate.value = date
            selectedTeamIds.value = teamIds

            if (date != null) {
                val startCalendar = Calendar.getInstance().apply {
                    set(date.year, date.monthValue - 1, date.dayOfMonth, 0, 0, 0)
                }
                val endCalendar = Calendar.getInstance().apply {
                    set(date.year, date.monthValue - 1, date.dayOfMonth, 23, 59, 59)
                }
                mainViewModel.eventViewModel.setDateRange(startCalendar, endCalendar)
            } else {
                val startDate = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_MONTH, -3)
                }
                val endDate = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_MONTH, 3)
                }
                mainViewModel.eventViewModel.setDateRange(startDate, endDate)
            }

            mainViewModel.eventViewModel.setSelectedTeams(teamIds)

            showFilterDialog.value = false

            onFilterApplied()
        }
    )
}

@Composable
fun MatchList(
    events: List<EventPredictionItem>,
    listState: LazyListState,
    authViewModel: AuthViewModel,
    onPredictionMade: () -> Unit,
    canRedact: Boolean
) {
    val groupItems = events.groupBy { it.date }
        .toList()
        .sortedByDescending { it.first }
        .toMap()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
    ) {
        groupItems.forEach { (date, cards) ->
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
                    onPredictionMade = onPredictionMade,
                    canRedact = canRedact
                )
            }
        }
    }
}