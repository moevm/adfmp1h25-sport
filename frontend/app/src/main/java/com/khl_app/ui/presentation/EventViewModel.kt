// EventViewModel.kt
import android.util.Log
import com.khl_app.domain.ApiClient
import com.khl_app.domain.models.EventPredictionItem
import com.khl_app.domain.models.EventResponse
import com.khl_app.domain.models.Team
import com.khl_app.storage.models.TeamData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.PrintWriter
import java.io.StringWriter
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

    private var currentStartDate = Calendar.getInstance()
    private var currentEndDate = Calendar.getInstance()

    init {
        currentStartDate.add(Calendar.DAY_OF_MONTH, -3)
        currentEndDate.add(Calendar.DAY_OF_MONTH, 3)
    }

    suspend fun loadEventsSequentially() {
        _isLoading.value = true
        Log.d("EventsViewModel", "Starting to load events")
        try {
            withContext(Dispatchers.IO) {
                Log.d("EventsViewModel", "Waiting for teams to load")
                val teamsLoaded = teamViewModel.awaitTeamsLoadedSequentially()
                Log.d("EventsViewModel", "Teams loaded status: $teamsLoaded")

                if (!teamsLoaded) {
                    _error.value = "Не удалось загрузить команды"
                    Log.e("EventsViewModel", "Failed to load teams")
                    return@withContext
                }

                val teamsMap = teamViewModel.teamsMap.first()
                Log.d("EventsViewModel", "Teams map size: ${teamsMap.size}")

                if (teamsMap.isEmpty()) {
                    _error.value = "Команды не загружены"
                    Log.e("EventsViewModel", "Teams map is empty")
                    return@withContext
                }

                val startTime = currentStartDate.timeInMillis / 1000
                val endTime = currentEndDate.timeInMillis / 1000
                val token = authViewModel.getAuthToken()
                Log.d("EventsViewModel", "Date range: $startTime to $endTime")

                if (token.isEmpty()) {
                    _error.value = "Cannot load events: Auth token is empty"
                    Log.e("EventsViewModel", "Auth token is empty")
                    return@withContext
                }

                Log.d("EventsViewModel", "Making API request to get events")
                try {
                    val response = ApiClient.apiService.getEvents(
                        token = token,
                        start = startTime,
                        end = endTime,
                        teams = emptyList()
                    )

                    Log.d("EventsViewModel", "Events API response code: ${response.code()}")

                    if (response.isSuccessful && response.body() != null) {
                        val eventsListRaw = response.body()!!.map { it.event }
                        Log.d("EventsViewModel", "Received ${eventsListRaw.size} events")

                        Log.d("EventsViewModel", "Making API request to get predictions")
                        try {
                            val predictionsResponse = ApiClient.apiService.getPredictions(
                                token = token,
                                userId = "current",
                                start = startTime,
                                end = endTime
                            )

                            Log.d("EventsViewModel", "Predictions API response code: ${predictionsResponse.code()}")
                            Log.d("EventsViewModel", "Predictions body: ${predictionsResponse.body().toString()}")

                            val predictionsMap = if (predictionsResponse.isSuccessful && predictionsResponse.body() != null) {
                                predictionsResponse.body()!!
                            } else {
                                Log.w("EventsViewModel", "Failed to get predictions: ${predictionsResponse.code()} - ${predictionsResponse.message()}")
                                emptyMap()
                            }

                            Log.d("EventsViewModel", "Mapping events with predictions")
                            val mappedEvents = mapEventsToEventPredictionItems(eventsListRaw, teamsMap, predictionsMap)
                            _events.value = mappedEvents
                            _error.value = null
                        } catch (e: Exception) {
                            Log.e("EventsViewModel", "Error fetching predictions", e)
                            // Continue with empty predictions
                            val mappedEvents = mapEventsToEventPredictionItems(eventsListRaw, teamsMap, emptyMap())
                            _events.value = mappedEvents
                            _error.value = null
                        }
                    } else {
                        val errorMsg = "Не удалось загрузить события: ${response.code()} - ${response.message()}"
                        _error.value = errorMsg
                        Log.e("EventsViewModel", errorMsg)
                    }
                } catch (e: Exception) {
                    Log.e("EventsViewModel", "Error during getEvents API call", e)
                    throw e // Re-throw to be caught by outer catch
                }
            }
        } catch (e: Exception) {
            Log.e("EventsViewModel", "Exception details:", e)
            // Log the full stack trace
            val sw = StringWriter()
            e.printStackTrace(PrintWriter(sw))
            Log.e("EventsViewModel", "Full stack trace: ${sw.toString()}")

            handleError(e, "Ошибка загрузки событий")
        } finally {
            _isLoading.value = false
            Log.d("EventsViewModel", "Finished loading events")
        }
    }

    suspend fun loadMorePastEventsSequentially() {
        currentStartDate.add(Calendar.DAY_OF_MONTH, -3)
        loadEventsSequentially()
    }

    suspend fun loadMoreFutureEventsSequentially() {
        currentEndDate.add(Calendar.DAY_OF_MONTH, 3)
        loadEventsSequentially()
    }

    private fun mapEventsToEventPredictionItems(
        events: List<EventResponse>,
        teamsMap: Map<String, TeamData>,
        predictionsMap: Map<String, Map<String, String>>
    ): List<EventPredictionItem> {
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return events.mapNotNull { event ->
            val teamAData = teamsMap[event.teamA.id.toString()]
            val teamBData = teamsMap[event.teamB.id.toString()]
            if (teamAData != null && teamBData != null) {
                val date = Date(event.startAt)
                Log.d("check", event.id.toString())
                val prediction = predictionsMap[event.startAtDay.toString()]?.get(event.id.toString())
                Log.d("check", prediction.toString())
                EventPredictionItem(
                    teamA = Team(teamAData.image, teamAData.name),
                    teamB = Team(teamBData.image, teamBData.name),
                    date = dateFormat.format(date),
                    time = timeFormat.format(date),
                    prediction = prediction,
                    result = if (event.score.isNotEmpty()) event.score else null,
                    period = event.period,
                    id = event.id
                )
            } else {
                null
            }
        }.sortedBy { it.date }
    }
}