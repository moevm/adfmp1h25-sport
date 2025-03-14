import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khl_app.domain.storage.IRepository
import com.khl_app.storage.models.TeamData
import com.khl_app.storage.models.TokenData
import kotlinx.coroutines.launch

class MainViewModel(
    private val application: Context,
    tokenCache: IRepository<TokenData>,
    teamCache: IRepository<TeamData>
) : ViewModel() {

    private val authViewModel = AuthViewModel(tokenCache)
    private val teamViewModel = TeamViewModel(application, teamCache, authViewModel)
    private val eventViewModel = EventViewModel(teamViewModel, authViewModel)

    // Методы делегирования для аутентификации
    fun login(login: String, password: String, onResult: (String?) -> Unit) {
        Log.d("MainViewModel", "Login attempt with login: $login")
        authViewModel.login(login, password) { error ->
            if (error == null) {
                Log.d("MainViewModel", "Login successful, proceeding to fetch teams")
                viewModelScope.launch {
                    try {
                        val teamError = teamViewModel.fetchAndSaveTeamsAsync()
                        if (teamError == null) {
                            Log.d("MainViewModel", "Teams loaded successfully, loading events")
                            eventViewModel.loadEventsAsync()
                        } else {
                            Log.e("MainViewModel", "Team loading failed: $teamError")
                        }
                        onResult(teamError)
                    } catch (e: Exception) {
                        Log.e("MainViewModel", "Exception during team loading", e)
                        onResult("Unexpected error: ${e.message}")
                    }
                }
            } else {
                Log.e("MainViewModel", "Login failed: $error")
                onResult(error)
            }
        }
    }

    fun register(login: String, password: String, onResult: (String?) -> Unit) {
        Log.d("MainViewModel", "Register attempt with login: $login")
        authViewModel.login(login, password) { error ->
            if (error == null) {
                Log.d("MainViewModel", "Registration successful, proceeding to fetch teams")
                viewModelScope.launch {
                    try {
                        val teamError = teamViewModel.fetchAndSaveTeamsAsync()
                        if (teamError == null) {
                            Log.d("MainViewModel", "Teams loaded successfully, loading events")
                            eventViewModel.loadEventsAsync()
                        } else {
                            Log.e("MainViewModel", "Team loading failed: $teamError")
                        }
                        onResult(teamError)
                    } catch (e: Exception) {
                        Log.e("MainViewModel", "Exception during team loading", e)
                        onResult("Unexpected error: ${e.message}")
                    }
                }
            } else {
                Log.e("MainViewModel", "Registration failed: $error")
                onResult(error)
            }
        }
    }

    fun checkTokenValidity(onResult: (Boolean) -> Unit) {
        Log.d("MainViewModel", "Checking token validity")
        authViewModel.checkTokenValidity { isValid ->
            if (isValid) {
                Log.d("MainViewModel", "Token is valid, fetching teams")
                viewModelScope.launch {
                    try {
                        val teamError = teamViewModel.fetchAndSaveTeamsAsync()
                        if (teamError == null) {
                            Log.d("MainViewModel", "Teams loaded successfully, loading events")
                            eventViewModel.loadEventsAsync()
                        } else {
                            Log.e("MainViewModel", "Team loading failed: $teamError")
                        }
                        onResult(true)
                    } catch (e: Exception) {
                        Log.e("MainViewModel", "Exception during team loading", e)
                        onResult(true) // Считаем, что токен валиден, даже если произошла ошибка загрузки команд
                    }
                }
            } else {
                Log.d("MainViewModel", "Token is invalid or expired")
                onResult(false)
            }
        }
    }

    // Делегированные свойства для событий
    val events = eventViewModel.events
    val isLoading = eventViewModel.isLoading
    val error = eventViewModel.error

    // Методы делегирования для событий
    fun loadEvents() {
        Log.d("MainViewModel", "Loading events, ensuring teams are loaded first")
        viewModelScope.launch {
            try {
                // Гарантируем, что команды загружены перед загрузкой событий
                val teamsLoaded = teamViewModel.awaitTeamsLoaded()
                if (teamsLoaded) {
                    Log.d("MainViewModel", "Teams are loaded, proceeding to load events")
                    eventViewModel.loadEventsAsync()
                } else {
                    Log.e("MainViewModel", "Cannot load events - teams failed to load")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Exception during event loading", e)
            }
        }
    }

    fun loadMorePastEvents() {
        Log.d("MainViewModel", "Loading more past events")
        eventViewModel.loadMorePastEvents()
    }

    fun loadMoreFutureEvents() {
        Log.d("MainViewModel", "Loading more future events")
        eventViewModel.loadMoreFutureEvents()
    }

    // Методы делегирования для работы с командами
    fun getCurrentTeam(onResult: (TeamData?) -> Unit) {
        Log.d("MainViewModel", "Getting current team - not implemented")
        // Здесь должна быть реализация получения текущей команды
    }
}