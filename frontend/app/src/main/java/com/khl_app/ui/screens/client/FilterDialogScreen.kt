package com.khl_app.ui.screens.client
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

/**
 * Диалоговое окно фильтрации событий по дате и командам
 */
@Composable
fun FilterDialog(
    show: Boolean,
    teams: List<TeamBasicInfo>,
    initialSelectedDate: LocalDate? = null,
    initialSelectedTeams: List<String> = emptyList(),
    onDismiss: () -> Unit,
    onApply: (selectedDate: LocalDate?, selectedTeams: List<String>) -> Unit
) {
    if (show) {
        var selectedDate by remember { mutableStateOf(initialSelectedDate) }
        var selectedTeams by remember { mutableStateOf(initialSelectedTeams) }
        var currentWeek by remember { mutableStateOf(LocalDate.now()) }
        var searchQuery by remember { mutableStateOf("") }

        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000)), // Полупрозрачный черный фон
                contentAlignment = Alignment.TopCenter // Изменено с Center на TopCenter
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(0.9f)
                        .padding(top = 40.dp), // Добавлен отступ сверху
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2F3E))
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Панель заголовка с кнопками закрытия и применения
                        DialogHeader(
                            onClose = onDismiss,
                            onApply = { onApply(selectedDate, selectedTeams) }
                        )

                        // Календарь по неделям
                        WeekCalendarView(
                            currentWeek = currentWeek,
                            selectedDate = selectedDate,
                            onPrevWeek = { currentWeek = currentWeek.minusWeeks(1) },
                            onNextWeek = { currentWeek = currentWeek.plusWeeks(1) },
                            onDateSelected = { date -> selectedDate = date },
                            onDateDeselected = { selectedDate = null }
                        )

                        Divider(
                            color = Color.Gray.copy(0.5f),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        // Секция выбора команд с поиском
                        TeamsSelectionSection(
                            teams = teams,
                            selectedTeams = selectedTeams,
                            searchQuery = searchQuery,
                            onSearchQueryChanged = { searchQuery = it },
                            onTeamSelectionChanged = { teamId, isSelected ->
                                selectedTeams = if (isSelected) {
                                    selectedTeams + teamId
                                } else {
                                    selectedTeams - teamId
                                }
                            },
                            onSelectAll = {
                                selectedTeams = if (selectedTeams.size == teams.size) {
                                    emptyList() // Снимаем все выделения
                                } else {
                                    teams.map { it.id } // Выбираем все команды
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogHeader(
    onClose: () -> Unit,
    onApply: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1D1F2B))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Кнопка закрытия
        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Закрыть",
                tint = Color.White
            )
        }

        // Кнопка применения
        IconButton(
            onClick = onApply,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Применить",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun WeekCalendarView(
    currentWeek: LocalDate,
    selectedDate: LocalDate?,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onDateDeselected: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Заголовок недели с кнопками навигации
        WeekHeader(
            currentWeek = currentWeek,
            onPrevWeek = onPrevWeek,
            onNextWeek = onNextWeek
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Дни недели
        WeekdaysHeader()

        Spacer(modifier = Modifier.height(8.dp))

        // Сетка дней недели
        WeekGrid(
            startDate = getStartOfWeek(currentWeek),
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
            onDateDeselected = onDateDeselected
        )
    }
}

@Composable
private fun WeekHeader(
    currentWeek: LocalDate,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit
) {
    val startOfWeek = getStartOfWeek(currentWeek)
    val endOfWeek = startOfWeek.plusDays(6)
    val formatter = DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevWeek) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Предыдущая неделя",
                tint = Color.White
            )
        }

        Text(
            text = "${startOfWeek.format(formatter)} - ${endOfWeek.format(formatter)}",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        IconButton(onClick = onNextWeek) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Следующая неделя",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun WeekdaysHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in DayOfWeek.values()) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val dayName = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru"))
                    .uppercase().take(2)

                Text(
                    text = dayName,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun WeekGrid(
    startDate: LocalDate,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDateDeselected: () -> Unit
) {
    val today = LocalDate.now()
    val weekDays = (0..6).map { startDate.plusDays(it.toLong()) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        weekDays.forEach { date ->
            DayCell(
                date = date,
                isSelected = date == selectedDate,
                isToday = date == today,
                onDateClick = { clickedDate ->
                    if (clickedDate == selectedDate) {
                        onDateDeselected()
                    } else {
                        onDateSelected(clickedDate)
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> Color(0xFF6C5CE7) // Фиолетовый для выбранной даты
                    isToday -> Color(0xFF1D1F2B).copy(alpha = 0.5f) // Полупрозрачный для сегодня
                    else -> Color.Transparent
                }
            )
            .border(
                width = if (isToday && !isSelected) 1.dp else 0.dp,
                color = if (isToday && !isSelected) Color(0xFF6C5CE7) else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onDateClick(date) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// Функция для получения начала недели (понедельник)
private fun getStartOfWeek(date: LocalDate): LocalDate {
    val fields = WeekFields.of(Locale.getDefault())
    val weekOfYear = date.get(fields.weekOfWeekBasedYear())
    return date
        .with(fields.weekOfWeekBasedYear(), weekOfYear.toLong())
        .with(fields.dayOfWeek(), 1)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamsSelectionSection(
    teams: List<TeamBasicInfo>,
    selectedTeams: List<String>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onTeamSelectionChanged: (String, Boolean) -> Unit,
    onSelectAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Заголовок и кнопка "Выбрать все"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Клубы",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            TextButton(
                onClick = onSelectAll,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF6C5CE7)
                )
            ) {
                Text(
                    text = if (selectedTeams.size == teams.size) "Снять все" else "Выбрать все",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Поле поиска
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            placeholder = { Text("Поиск команд", color = Color.White.copy(alpha = 0.5f)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Поиск",
                    tint = Color.White.copy(alpha = 0.7f)
                )
            },
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (teams.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет доступных команд",
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Фильтруем команды по поисковому запросу
                val filteredTeams = if (searchQuery.isEmpty()) {
                    teams
                } else {
                    teams.filter { it.name.contains(searchQuery, ignoreCase = true) }
                }

                if (filteredTeams.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Команды не найдены",
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Группируем команды по дивизионам
                    val teamsByDivision = filteredTeams.groupBy {
                        it.division ?: "Без дивизиона"
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        teamsByDivision.forEach { (division, teamsInDivision) ->
                            // Заголовок дивизиона
                            item {
                                Text(
                                    text = division,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            // Команды в дивизионе
                            items(teamsInDivision) { team ->
                                TeamRow(
                                    team = team,
                                    isSelected = selectedTeams.contains(team.id),
                                    onSelectionChanged = { isSelected ->
                                        onTeamSelectionChanged(team.id, isSelected)
                                    }
                                )

                                Divider(
                                    color = Color.Gray.copy(0.3f),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamRow(
    team: TeamBasicInfo,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onSelectionChanged(!isSelected) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Логотип команды в круге
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        ) {
            // Проверяем, существует ли файл логотипа
            if (team.logoUrl.isNotEmpty() && File(team.logoUrl).exists()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(team.logoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Логотип ${team.name}",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = "logo",
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }

        // Название команды
        Text(
            text = team.name,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        )

        // Индикатор выбора
        RadioButton(
            selected = isSelected,
            onClick = { onSelectionChanged(!isSelected) },
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF6C5CE7),
                unselectedColor = Color.White.copy(alpha = 0.5f)
            )
        )
    }
}

/**
 * Расширенная модель для представления информации о команде с дивизионом
 */
data class TeamBasicInfo(
    val id: String,
    val name: String,
    val logoUrl: String,
    val division: String
)