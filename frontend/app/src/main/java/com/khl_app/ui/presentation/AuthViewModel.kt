import android.util.Log
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
        print("bla bla")
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

    fun checkTokenValidity(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                // Get cached token
                val token = tokenCache.getInfo().first()

                // Debug log
                Log.d("TokenCheck", "Retrieved token: ${token.accessToken.take(10)}... (length: ${token.accessToken.length})")

                // If no token, return false
                if (token.accessToken.isEmpty()) {
                    Log.w("TokenCheck", "No access token found")
                    onResult(false)
                    return@launch
                }

                // Check token validity with API
                Log.d("TokenCheck", "Checking token validity...")
                val response = ApiClient.apiService.isTokenValid("Bearer ${token.accessToken}")

                if (response.isSuccessful) {
                    Log.d("TokenCheck", "Token is valid")
                    onResult(true)
                } else {
                    Log.e("TokenCheck", "Token validation failed: ${response.code()} - ${response.message()}")
                    // Try refresh token if available
                    if (token.refreshToken.isNotEmpty()) {
                        Log.d("TokenCheck", "Attempting to refresh token...")
                        refreshToken(token.refreshToken) { refreshSuccess ->
                            Log.d("TokenCheck", "Token refresh result: $refreshSuccess")
                            onResult(refreshSuccess)
                        }
                    } else {
                        Log.w("TokenCheck", "No refresh token available")
                        onResult(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("TokenCheck", "Exception during token validation: ${e.message}", e)
                onResult(false)
            }
        }
    }

    private fun refreshToken(refreshToken: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.refresh("Bearer $refreshToken")

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        // Save new tokens
                        tokenCache.saveInfo(
                            TokenData(
                                accessToken = loginResponse.accessToken,
                                refreshToken = loginResponse.refreshToken
                            )
                        )
                        onResult(true)
                    } ?: onResult(false)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
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