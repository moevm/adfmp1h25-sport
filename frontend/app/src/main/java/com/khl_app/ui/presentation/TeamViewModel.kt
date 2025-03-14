import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.viewModelScope
import com.khl_app.domain.ApiClient
import com.khl_app.domain.models.TeamWrapper
import com.khl_app.domain.storage.IRepository
import com.khl_app.storage.models.TeamData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class TeamViewModel(
    private val context: Context,
    private val teamCache: IRepository<TeamData>,
    private val authViewModel: AuthViewModel
) : BaseViewModel() {

    // Кэш команд для быстрого доступа
    private val _teamsMap = MutableStateFlow<Map<String, TeamData>>(emptyMap())
    val teamsMap: StateFlow<Map<String, TeamData>> = _teamsMap

    init {
        viewModelScope.launch {
            loadTeamsToCache()
        }
    }

    fun fetchAndSaveTeams(onResult: (String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = authViewModel.getAuthToken()
                if (token.isEmpty()) {
                    val errorMsg = "Cannot fetch teams: Auth token is empty"
                    _error.value = errorMsg
                    onResult(errorMsg)
                    return@launch
                }

                val response = ApiClient.apiService.getTeams(token)

                if (response.isSuccessful && response.body() != null) {
                    processTeams(response.body()!!, onResult)
                } else {
                    val errorMsg = "Failed to fetch teams: ${response.code()} - ${response.message()}"
                    _error.value = errorMsg
                    onResult(errorMsg)
                }
            } catch (e: Exception) {
                handleError(e, "Error fetching teams")
                onResult(e.message)
            } finally {
                _isLoading.value = false
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

                // Создаем новую карту для обновления
                val newTeamsMap = _teamsMap.value.toMutableMap()

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
                    // Добавляем в новую карту
                    newTeamsMap[team.id.toString()] = teamData
                }

                // Обновляем StateFlow
                withContext(Dispatchers.Main) {
                    _teamsMap.value = newTeamsMap
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

    private suspend fun loadTeamsToCache() {
        try {
            val tempMap = mutableMapOf<String, TeamData>()

            teamCache.getInfo().collect { teamData ->
                if (teamData != null) {
                    // Добавляем каждый элемент во временную карту
                    tempMap[teamData.id.toString()] = teamData
                }
            }

            // Обновляем StateFlow, присваивая ему новую карту
            _teamsMap.value = tempMap
        } catch (e: Exception) {
            val errorMsg = "Ошибка загрузки команд: ${e.message}"
            println(errorMsg)
            _error.value = errorMsg
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
}