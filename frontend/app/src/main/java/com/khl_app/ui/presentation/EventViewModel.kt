import androidx.lifecycle.viewModelScope
import com.khl_app.domain.ApiClient
import com.khl_app.domain.models.EventPredictionItem
import com.khl_app.domain.models.EventResponse
import com.khl_app.domain.models.Team
import com.khl_app.storage.models.TeamData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EventViewModel(
    private val teamViewModel: TeamViewModel,
    private val authViewModel: AuthViewModel
) : BaseViewModel() {

    private val _events = MutableStateFlow<List<EventPredictionItem>>(emptyList())
    val events: StateFlow<List<EventPredictionItem>> = _events

    // Текущий диапазон дат для загрузки событий
    private var currentStartDate = Calendar.getInstance()
    private var currentEndDate = Calendar.getInstance()

    init {
        // Установка начального диапазона дат (3 дня до и после текущей даты)
        currentStartDate.add(Calendar.DAY_OF_MONTH, -3)
        currentEndDate.add(Calendar.DAY_OF_MONTH, 3)
    }

    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Ждем, пока загрузятся команды
                val teamsMap = teamViewModel.teamsMap.first()
                if (teamsMap.isEmpty()) {
                    println("Teams cache is empty")
                    _error.value = "Команды не загружены"
                    _isLoading.value = false
                    return@launch
                }

                val startTime = currentStartDate.timeInMillis / 1000
                val endTime = currentEndDate.timeInMillis / 1000
                val token = authViewModel.getAuthToken()

                if (token.isEmpty()) {
                    _error.value = "Cannot load events: Auth token is empty"
                    _isLoading.value = false
                    return@launch
                }

                val response = ApiClient.apiService.getEvents(
                    token = token, start = startTime, end = endTime, teams = emptyList()
                )

                if (response.isSuccessful && response.body() != null) {
                    val events = response.body()!!.map { it.event }
                    val eventsList = mapEventsToEventPredictionItems(events, teamsMap)
                    _events.value = eventsList
                    _error.value = null
                } else {
                    _error.value = "Не удалось загрузить события: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                handleError(e, "Ошибка загрузки событий")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMorePastEvents() {
        currentStartDate.add(Calendar.DAY_OF_MONTH, -3)
        loadEvents()
    }

    fun loadMoreFutureEvents() {
        currentEndDate.add(Calendar.DAY_OF_MONTH, 3)
        loadEvents()
    }

    private fun mapEventsToEventPredictionItems(
        events: List<EventResponse>,
        teamsMap: Map<String, TeamData>
    ): List<EventPredictionItem> {
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