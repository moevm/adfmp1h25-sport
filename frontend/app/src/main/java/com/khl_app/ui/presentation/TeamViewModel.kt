import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.khl_app.domain.ApiClient
import com.khl_app.domain.models.TeamWrapper
import com.khl_app.domain.storage.IRepository
import com.khl_app.storage.models.TeamData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.toList
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

    // Флаг готовности кеша команд
    private val _teamsLoaded = MutableStateFlow(false)
    val teamsLoaded: StateFlow<Boolean> = _teamsLoaded

    init {
        viewModelScope.launch {
            loadTeamsToCache()
        }
    }

    fun fetchAndSaveTeams(onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val result = fetchAndSaveTeamsAsync()
            onResult(result)
        }
    }

    // Асинхронная версия для использования в suspend функциях
    suspend fun fetchAndSaveTeamsAsync(): String? {
        _isLoading.value = true
        try {
            val token = authViewModel.getAuthToken()
            if (token.isEmpty()) {
                val errorMsg = "Cannot fetch teams: Auth token is empty"
                _error.value = errorMsg
                return errorMsg
            }

            Log.d("TeamViewModel", "Fetching teams from API...")
            val response = ApiClient.apiService.getTeams(token)

            if (response.isSuccessful && response.body() != null) {
                Log.d("TeamViewModel", "Received ${response.body()!!.size} teams from API")
                return processTeamsAsync(response.body()!!)
            } else {
                val errorMsg = "Failed to fetch teams: ${response.code()} - ${response.message()}"
                _error.value = errorMsg
                Log.e("TeamViewModel", errorMsg)
                return errorMsg
            }
        } catch (e: Exception) {
            handleError(e, "Error fetching teams")
            return e.message
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun processTeamsAsync(wrappers: List<TeamWrapper>): String? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("TeamViewModel", "Processing ${wrappers.size} teams...")
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
                    // Добавляем новую информацию в карту
                    newTeamsMap[team.id.toString()] = teamData
                }

                // Обновляем StateFlow на главном потоке
                withContext(Dispatchers.Main) {
                    _teamsMap.value = newTeamsMap
                    _teamsLoaded.value = true
                    Log.d("TeamViewModel", "Teams map updated with ${newTeamsMap.size} teams")
                }

                Log.d("TeamViewModel", "Teams processed and saved to cache")
                return@withContext null // Нет ошибки
            } catch (e: Exception) {
                val errorMsg = "Error processing teams: ${e.message}"
                Log.e("TeamViewModel", errorMsg, e)
                e.printStackTrace()
                return@withContext errorMsg
            }
        }
    }

    // Для обратной совместимости сохраняем оригинальную сигнатуру
    private suspend fun processTeams(wrappers: List<TeamWrapper>, onResult: (String?) -> Unit) {
        val error = processTeamsAsync(wrappers)
        withContext(Dispatchers.Main) {
            onResult(error)
        }
    }

    suspend fun loadTeamsToCache(): Boolean {
        try {
            Log.d("TeamViewModel", "Loading teams from cache...")

            // Собираем все команды из репозитория
            val teams = teamCache.getInfo().toList().filterNotNull()

            val tempMap = mutableMapOf<String, TeamData>()
            teams.forEach { teamData ->
                tempMap[teamData.id.toString()] = teamData
            }

            // Обновляем StateFlow
            _teamsMap.value = tempMap

            // Устанавливаем флаг, что команды загружены
            val loaded = tempMap.isNotEmpty()
            _teamsLoaded.value = loaded

            Log.d("TeamViewModel", "Teams loaded from cache: ${tempMap.size} teams, loaded flag: $loaded")
            return loaded
        } catch (e: Exception) {
            val errorMsg = "Ошибка загрузки команд: ${e.message}"
            Log.e("TeamViewModel", errorMsg, e)
            _error.value = errorMsg
            _teamsLoaded.value = false
            return false
        }
    }

    // Метод для ожидания загрузки команд
    suspend fun awaitTeamsLoaded(): Boolean {
        Log.d("TeamViewModel", "Awaiting teams loaded, current status: ${_teamsLoaded.value}")

        if (_teamsLoaded.value) return true

        // Если команды не загружены, пробуем загрузить их из кеша
        if (_teamsMap.value.isEmpty()) {
            Log.d("TeamViewModel", "Teams map is empty, trying to load from cache")
            val loaded = loadTeamsToCache()
            if (loaded) {
                Log.d("TeamViewModel", "Successfully loaded teams from cache")
                return true
            } else {
                Log.d("TeamViewModel", "Failed to load teams from cache")
            }
        }

        return _teamsLoaded.value
    }

    private suspend fun downloadAndSaveImage(imageUrl: String, teamId: String, directory: File): String {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "team_logo_$teamId.png"
                val file = File(directory, fileName)

                // Если файл уже существует, возвращаем его путь
                if (file.exists()) {
                    return@withContext file.absolutePath
                }

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

                return@withContext file.absolutePath
            } catch (e: Exception) {
                Log.e("TeamViewModel", "Error downloading image $imageUrl: ${e.message}", e)
                return@withContext ""
            }
        }
    }
}