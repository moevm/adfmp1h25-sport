// TeamViewModel.kt
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.khl_app.domain.ApiClient
import com.khl_app.domain.models.TeamWrapper
import com.khl_app.domain.storage.IRepository
import com.khl_app.storage.models.TeamData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class TeamViewModel(
    private val context: Context,
    private val teamCache: IRepository<TeamData>,
    private val authViewModel: AuthViewModel
) : BaseViewModel() {

    private val _teamsMap = MutableStateFlow<Map<String, TeamData>>(emptyMap())
    val teamsMap: StateFlow<Map<String, TeamData>> = _teamsMap

    private val _teamsLoaded = MutableStateFlow(false)
    val teamsLoaded: StateFlow<Boolean> = _teamsLoaded

    suspend fun fetchAndSaveTeamsSequentially(): String? {
        _isLoading.value = true
        return try {
            withContext(Dispatchers.IO) {
                val token = authViewModel.getAuthToken()
                if (token.isEmpty()) {
                    val errorMsg = "Cannot fetch teams: Auth token is empty"
                    _error.value = errorMsg
                    return@withContext errorMsg
                }

                val response = ApiClient.apiService.getTeams(token)
                if (response.isSuccessful && response.body() != null) {
                    processTeamsSequentially(response.body()!!)
                } else {
                    val errorMsg = "Failed to fetch teams: ${response.code()} - ${response.message()}"
                    _error.value = errorMsg
                    errorMsg
                }
            }
        } catch (e: Exception) {
            handleError(e, "Error fetching teams")
            e.message
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun processTeamsSequentially(wrappers: List<TeamWrapper>): String? {
        return withContext(Dispatchers.IO) {
            try {
                val logoDir = File(context.filesDir, "team_logos")
                if (!logoDir.exists()) {
                    logoDir.mkdirs()
                }

                val newTeamsMap = mutableMapOf<String, TeamData>()
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
                    newTeamsMap[team.id.toString()] = teamData
                }

                _teamsMap.value = newTeamsMap
                _teamsLoaded.value = true
                null
            } catch (e: Exception) {
                val errorMsg = "Error processing teams: ${e.message}"
                _error.value = errorMsg
                errorMsg
            }
        }
    }

    suspend fun loadTeamsFromCache(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val teams = teamCache.getInfo().toList().filterNotNull()
                val tempMap = mutableMapOf<String, TeamData>()
                teams.forEach { teamData ->
                    tempMap[teamData.id.toString()] = teamData
                }
                _teamsMap.value = tempMap
                val loaded = tempMap.isNotEmpty()
                _teamsLoaded.value = loaded
                loaded
            } catch (e: Exception) {
                val errorMsg = "Ошибка загрузки команд: ${e.message}"
                _error.value = errorMsg
                _teamsLoaded.value = false
                false
            }
        }
    }

    suspend fun awaitTeamsLoadedSequentially(): Boolean {
        if (_teamsLoaded.value) return true
        if (_teamsMap.value.isEmpty()) {
            val loaded = loadTeamsFromCache()
            if (loaded) return true
        }
        return _teamsLoaded.value
    }

    private suspend fun downloadAndSaveImage(imageUrl: String, teamId: String, directory: File): String {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "team_logo_$teamId.png"
                val file = File(directory, fileName)
                if (file.exists()) {
                    file.absolutePath
                } else {
                    val url = URL(imageUrl)
                    val connection = url.openConnection()
                    connection.connectTimeout = 5000
                    connection.readTimeout = 5000
                    connection.connect()
                    val inputStream = connection.getInputStream()
                    val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()
                    val outputStream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()
                    file.absolutePath
                }
            } catch (e: Exception) {
                ""
            }
        }
    }
}