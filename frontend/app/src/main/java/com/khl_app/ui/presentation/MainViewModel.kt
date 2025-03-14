import android.content.Context
import androidx.lifecycle.ViewModel
import com.khl_app.domain.storage.IRepository
import com.khl_app.storage.models.TeamData
import com.khl_app.storage.models.TokenData

class MainViewModel(
    private val application: Context,
    tokenCache: IRepository<TokenData>,
    teamCache: IRepository<TeamData>
) : ViewModel() {

    private val authViewModel = AuthViewModel(tokenCache)
    private val teamViewModel = TeamViewModel(application, teamCache, authViewModel)
    private val eventViewModel = EventViewModel(teamViewModel, authViewModel)

    // Auth delegating methods
    fun login(login: String, password: String, onResult: (String?) -> Unit) {
        authViewModel.login(login, password) { error ->
            if (error == null) {
                // Если логин успешен, загружаем команды
                teamViewModel.fetchAndSaveTeams { teamError ->
                    if (teamError == null) {
                        // Если команды загрузились, загружаем события
                        eventViewModel.loadEvents()
                    }
                    onResult(teamError)
                }
            } else {
                onResult(error)
            }
        }
    }

    fun register(login: String, password: String, onResult: (String?) -> Unit) {
        // Аналогично методу login
    }

    // Events delegating properties
    val events = eventViewModel.events
    val isLoading = eventViewModel.isLoading
    val error = eventViewModel.error

    // Events delegating methods
    fun loadEvents() = eventViewModel.loadEvents()
    fun loadMorePastEvents() = eventViewModel.loadMorePastEvents()
    fun loadMoreFutureEvents() = eventViewModel.loadMoreFutureEvents()

    // Team delegating methods
    fun getCurrentTeam(onResult: (TeamData?) -> Unit) {
        // Реализация
    }
}