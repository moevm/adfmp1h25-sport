import androidx.lifecycle.viewModelScope
import com.khl_app.domain.ApiClient
import com.khl_app.domain.storage.IRepository
import com.khl_app.storage.models.TokenData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthViewModel(
    private val tokenCache: IRepository<TokenData>
) : BaseViewModel() {

    fun login(login: String, password: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiClient.apiService.login(mapOf("login" to login, "password" to password))
                if (response.isSuccessful && response.body() != null) {
                    val tokenData = TokenData(response.body()!!.accessToken, response.body()!!.refreshToken)
                    tokenCache.saveInfo(tokenData)
                    onResult(null)
                } else {
                    val errorMsg = "Login failed: ${response.code()} - ${response.message()}"
                    _error.value = errorMsg
                    onResult(errorMsg)
                }
            } catch (e: Exception) {
                handleError(e, "Login exception")
                onResult(e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(login: String, password: String, onResult: (String?) -> Unit) {
        // Аналогично методу login
    }

    suspend fun getAuthToken(): String {
        return try {
            val tokenData = tokenCache.getInfo().first()
            "Bearer ${tokenData.accessToken}"
        } catch (e: Exception) {
            println("Error getting auth token: ${e.message}")
            ""
        }
    }
}