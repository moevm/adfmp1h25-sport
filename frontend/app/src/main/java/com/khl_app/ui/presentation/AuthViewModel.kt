// AuthViewModel.kt
import com.khl_app.domain.ApiClient
import com.khl_app.domain.storage.IRepository
import com.khl_app.storage.models.TokenData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class AuthViewModel(
    private val tokenCache: IRepository<TokenData>
) : BaseViewModel() {

    suspend fun loginSuspend(login: String, password: String): String? {
        _isLoading.value = true
        return try {
            withContext(Dispatchers.IO) {
                val response = ApiClient.apiService.login(mapOf("login" to login, "password" to password))
                if (response.isSuccessful && response.body() != null) {
                    val tokenData = TokenData(
                        accessToken = response.body()!!.accessToken,
                        refreshToken = response.body()!!.refreshToken
                    )
                    tokenCache.saveInfo(tokenData)
                    null
                } else {
                    val errorMsg = "Login failed: ${response.code()} - ${response.message()}"
                    _error.value = errorMsg
                    errorMsg
                }
            }
        } catch (e: Exception) {
            handleError(e, "Login exception")
            e.message
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun registerSuspend(login: String, password: String): String? {
        return loginSuspend(login, password)
    }

    suspend fun checkTokenValiditySuspend(): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val token = tokenCache.getInfo().first()
                if (token.accessToken.isEmpty()) {
                    false
                } else {
                    val response = ApiClient.apiService.isTokenValid("Bearer ${token.accessToken}")
                    if (response.isSuccessful) {
                        true
                    } else {
                        if (token.refreshToken.isNotEmpty()) {
                            refreshTokenSuspend(token.refreshToken)
                        } else {
                            false
                        }
                    }
                }
            }
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun refreshTokenSuspend(refreshToken: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val response = ApiClient.apiService.refresh("Bearer $refreshToken")
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        tokenCache.saveInfo(
                            TokenData(
                                accessToken = loginResponse.accessToken,
                                refreshToken = loginResponse.refreshToken
                            )
                        )
                        true
                    } ?: false
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getAuthToken(): String {
        return try {
            withContext(Dispatchers.IO) {
                val tokenData = tokenCache.getInfo().first()
                "Bearer ${tokenData.accessToken}"
            }
        } catch (e: Exception) {
            ""
        }
    }
}