// MainViewModel.kt
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khl_app.domain.ApiClient
import com.khl_app.domain.storage.IRepository
import com.khl_app.storage.models.TeamData
import com.khl_app.storage.models.TokenData
import com.khl_app.ui.presentation.FollowersViewModel
import kotlinx.coroutines.launch

class MainViewModel(
    private val application: Context,
    private val tokenCache: IRepository<TokenData>,
    teamCache: IRepository<TeamData>
) : ViewModel() {

    val authViewModel = AuthViewModel(tokenCache)
    val teamViewModel = TeamViewModel(application, teamCache, authViewModel)
    val eventViewModel = EventViewModel(teamViewModel, authViewModel)
    val followersViewModel = FollowersViewModel(ApiClient.apiService, tokenCache)

    fun login(login: String, password: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val authError = authViewModel.loginSuspend(login, password)
            if (authError == null) {
                val teamError = teamViewModel.fetchAndSaveTeamsSequentially()
                if (teamError == null) {
                    eventViewModel.loadEventsSequentially()
                }
                onResult(teamError)
            } else {
                onResult(authError)
            }
        }
    }

    fun register(login: String, password: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val regError = authViewModel.registerSuspend(login, password)
            if (regError == null) {
                val teamError = teamViewModel.fetchAndSaveTeamsSequentially()
                if (teamError == null) {
                    eventViewModel.loadEventsSequentially()
                }
                onResult(teamError)
            } else {
                onResult(regError)
            }
        }
    }

    fun checkTokenValidity(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val valid = authViewModel.checkTokenValiditySuspend()
            if (valid) {
                val teamError = teamViewModel.fetchAndSaveTeamsSequentially()
                if (teamError == null) {
                    eventViewModel.loadEventsSequentially()
                }
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    val events = eventViewModel.events
    val isLoading = eventViewModel.isLoading
    val error = eventViewModel.error

    fun loadEvents() {
        viewModelScope.launch {
            val teamsLoaded = teamViewModel.awaitTeamsLoadedSequentially()
            if (teamsLoaded) {
                eventViewModel.loadEventsSequentially()
            }
        }
    }

    fun loadMorePastEvents() {
        viewModelScope.launch {
            eventViewModel.loadMorePastEventsSequentially()
        }
    }

    fun loadMoreFutureEvents() {
        viewModelScope.launch {
            eventViewModel.loadMoreFutureEventsSequentially()
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenCache.deleteInfo()
        }
    }

    fun getCurrentTeam(onResult: (TeamData?) -> Unit) {
        onResult(null)
    }
}