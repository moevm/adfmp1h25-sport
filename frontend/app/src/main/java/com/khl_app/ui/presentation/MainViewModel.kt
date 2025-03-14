package com.khl_app.ui.view_models

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khl_app.domain.ApiClient
import com.khl_app.domain.models.EventPredictionItem
import com.khl_app.domain.models.EventResponse
import com.khl_app.domain.models.Team
import com.khl_app.domain.models.TeamResponse
import com.khl_app.domain.models.TeamWrapper
import com.khl_app.domain.storage.IRepository
import com.khl_app.storage.models.TeamData
import com.khl_app.storage.models.TokenData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class MainViewModel(
    private val context: Context,
    private val tokenCache: IRepository<TokenData>,
    private val teamCache: IRepository<TeamData>,
) : ViewModel() {

    // События (Event) - состояния
    private val _events = MutableStateFlow<List<EventPredictionItem>>(emptyList())
    val events: StateFlow<List<EventPredictionItem>> = _events

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Кэш команд для быстрого доступа (ключ - id команды в виде строки)
    private val teamsMap = mutableMapOf<String, TeamData>()

    // Текущий диапазон дат для загрузки событий
    private var currentStartDate = Calendar.getInstance()
    private var currentEndDate = Calendar.getInstance()

    init {
        // Установка начального диапазона дат (3 дня до и после текущей даты)
        currentStartDate.add(Calendar.DAY_OF_MONTH, -3)
        currentEndDate.add(Calendar.DAY_OF_MONTH, 3)

        // Загрузка команд при инициализации
        viewModelScope.launch {
            loadTeamsToCache()
        }
    }

    // Получение JWT токена с помощью first() для ожидания первого элемента Flow
    private suspend fun getAuthToken(): String {
        return try {
            val tokenData = tokenCache.getInfo().first()
            "Bearer ${tokenData.accessToken}"
        } catch (e: Exception) {
            println("Error getting auth token: ${e.message}")
            ""
        }
    }

    fun login(login: String, password: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response =
                    ApiClient.apiService.login(mapOf("login" to login, "password" to password))
                if (response.isSuccessful && response.body() != null) {
                    val tokenData = TokenData(response.body()!!.accessToken, response.body()!!.refreshToken)
                    tokenCache.saveInfo(tokenData)
                    println("Login successful, token saved: ${tokenData.accessToken.take(10)}...")

                    // Теперь правильно получаем токен и загружаем команды
                    fetchAndSaveTeams { error ->
                        if (error != null) {
                            println("Failed to load teams: $error")
                            _error.value = error
                            onResult(error)
                        } else {
                            println("Teams loaded successfully, loading events...")
                            // После успешной загрузки команд, запускаем загрузку событий
                            loadEvents()
                            onResult(null)
                        }
                    }
                } else {
                    val errorMsg = "Login failed: ${response.code()} - ${response.message()}"
                    println(errorMsg)
                    onResult(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Login exception: ${e.message}"
                println(errorMsg)
                e.printStackTrace()
                onResult(errorMsg)
            }
        }
    }

    fun register(login: String, password: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response =
                    ApiClient.apiService.register(mapOf("login" to login, "password" to password))
                if (response.isSuccessful && response.body() != null) {
                    val tokenData = TokenData(response.body()!!.accessToken, response.body()!!.refreshToken)
                    tokenCache.saveInfo(tokenData)
                    println("Registration successful, token saved: ${tokenData.accessToken.take(10)}...")

                    fetchAndSaveTeams { error ->
                        if (error != null) {
                            println("Failed to load teams: $error")
                            _error.value = error
                            onResult(error)
                        } else {
                            println("Teams loaded successfully, loading events...")
                            // После успешной загрузки команд, загружаем события
                            loadEvents()
                            onResult(null)
                        }
                    }
                } else {
                    val errorMsg = "Registration failed: ${response.code()} - ${response.message()}"
                    println(errorMsg)
                    onResult(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Registration exception: ${e.message}"
                println(errorMsg)
                e.printStackTrace()
                onResult(errorMsg)
            }
        }
    }

    fun fetchAndSaveTeams(onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val token = getAuthToken()
                if (token.isEmpty()) {
                    val errorMsg = "Cannot fetch teams: Auth token is empty"
                    println(errorMsg)
                    onResult(errorMsg)
                    return@launch
                }

                println("Fetching teams with token: ${token.take(15)}...")
                val response = ApiClient.apiService.getTeams(token)

                if (response.isSuccessful && response.body() != null) {
                    println("Teams fetched successfully: ${response.body()!!.size} teams")
                    processTeams(response.body()!!, onResult)
                } else {
                    val errorMsg = "Failed to fetch teams: ${response.code()} - ${response.message()}"
                    println(errorMsg)
                    onResult(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Error fetching teams: ${e.message}"
                println(errorMsg)
                e.printStackTrace()
                onResult(errorMsg)
            }
        }
    }

    private suspend fun processTeams(wrappers: List<TeamWrapper>, onResult: (String?) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                println("Processing ${wrappers.size} teams...")
                val logoDir = File(context.filesDir, "team_logos")
                if (!logoDir.exists()) {
                    logoDir.mkdirs()
                }

                for (wrapper in wrappers) {
                    val team = wrapper.team
                    val localImagePath = downloadAndSaveImage(team.image, team.id.toString(), logoDir)

                    val teamData = TeamData(
                        conference = team.conference,
                        conferenceKey = team.conferenceKey,
                        division = team.division,
                        divisionKey = team.divisionKey,
                        id = team.id,
                        image = localImagePath,
                        khlID = team.khlID,
                        location = team.location,
                        name = team.name
                    )

                    teamCache.saveInfo(teamData)
                    // Сохраняем команду в кэш с использованием строкового ключа
                    teamsMap[team.id.toString()] = teamData
                }

                println("Teams processed and saved to cache")
                withContext(Dispatchers.Main) {
                    onResult(null)
                }
            } catch (e: Exception) {
                val errorMsg = "Error processing teams: ${e.message}"
                println(errorMsg)
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResult(errorMsg)
                }
            }
        }
    }

    private suspend fun downloadAndSaveImage(imageUrl: String, teamId: String, directory: File): String {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "team_logo_$teamId.png"
                val file = File(directory, fileName)

                val url = URL(imageUrl)
                val connection = url.openConnection()
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.connect()

                val inputStream = connection.getInputStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()

                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                return@withContext file.absolutePath
            } catch (e: Exception) {
                println("Error downloading image $imageUrl: ${e.message}")
                e.printStackTrace()
                return@withContext ""
            }
        }
    }

    fun getCurrentTeam(onResult: (TeamData?) -> Unit) {
        viewModelScope.launch {
            try {
                val teamData = teamCache.getInfo().first()
                onResult(teamData)
            } catch (e: Exception) {
                println("Error getting current team: ${e.message}")
                onResult(null)
            }
        }
    }

    // Загрузка команд в кэш из локального хранилища
    private suspend fun loadTeamsToCache() {
        try {
            teamCache.getInfo().collect { teamData ->
                if (teamData != null) {
                    // Сохраняем id как строку для согласованности
                    teamsMap[teamData.id.toString()] = teamData
                }
            }
        } catch (e: Exception) {
            val errorMsg = "Ошибка загрузки команд: ${e.message}"
            println(errorMsg)
            _error.value = errorMsg
        }
    }

    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Если кэш команд пуст, загружаем команды
                if (teamsMap.isEmpty()) {
                    println("Teams cache is empty, loading teams...")
                    loadTeamsToCache()
                }

                val startTime = currentStartDate.timeInMillis / 1000
                val endTime = currentEndDate.timeInMillis / 1000
                val token = getAuthToken()

                if (token.isEmpty()) {
                    val errorMsg = "Cannot load events: Auth token is empty"
                    println(errorMsg)
                    _error.value = errorMsg
                    _isLoading.value = false
                    return@launch
                }

                println("Loading events with token: ${token.take(15)}..., start: $startTime, end: $endTime")
                val response = ApiClient.apiService.getEvents(
                    token = token,
                    start = startTime,
                    end = endTime,
                    teams = emptyList() // Получаем события для всех команд
                )

                if (response.isSuccessful && response.body() != null) {
                    // Извлекаем события из обертки
                    val events = response.body()!!.map { it.event }
                    val eventsList = mapEventsToEventPredictionItems(events)
                    _events.value = eventsList
                    _error.value = null
                    println("Events loaded successfully: ${eventsList.size} events")
                } else {
                    val errorMsg = "Не удалось загрузить события: ${response.code()} - ${response.message()}"
                    println(errorMsg)
                    _error.value = errorMsg
                }
            } catch (e: Exception) {
                val errorMsg = "Ошибка загрузки событий: ${e.message}"
                println(errorMsg)
                e.printStackTrace()
                _error.value = errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Загрузка более ранних событий (на 3 дня назад)
    fun loadMorePastEvents() {
        // Смещаем начальную дату на 3 дня назад
        currentStartDate.add(Calendar.DAY_OF_MONTH, -3)
        println("Loading past events, new start date: ${SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(currentStartDate.time)}")
        loadEvents()
    }

    // Загрузка более поздних событий (на 3 дня вперед)
    fun loadMoreFutureEvents() {
        // Смещаем конечную дату на 3 дня вперед
        currentEndDate.add(Calendar.DAY_OF_MONTH, 3)
        println("Loading future events, new end date: ${SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(currentEndDate.time)}")
        loadEvents()
    }

    // Преобразование ответа API в модели для отображения
    private fun mapEventsToEventPredictionItems(events: List<EventResponse>): List<EventPredictionItem> {
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        println("Mapping ${events.size} events to UI model")
        return events.mapNotNull { event ->
            // Используем id команд как строки для поиска в кэше
            val teamAData = teamsMap[event.teamA.id.toString()]
            val teamBData = teamsMap[event.teamB.id.toString()]

            if (teamAData != null && teamBData != null) {
                val date = Date(event.startAt) // Используем startAt напрямую как timestamp

                EventPredictionItem(
                    teamA = Team(teamAData.image, teamAData.name),
                    teamB = Team(teamBData.image, teamBData.name),
                    date = dateFormat.format(date),
                    time = timeFormat.format(date),
                    prediction = null, // Пока нет прогноза
                    result = if (event.score.isNotEmpty()) event.score else null
                )
            } else {
                println("Missing team data for event, teamA: ${event.teamA.id}, teamB: ${event.teamB.id}")
                null // Пропускаем события, для которых нет информации о командах
            }
        }.sortedBy { it.date } // Сортируем по дате
    }
}