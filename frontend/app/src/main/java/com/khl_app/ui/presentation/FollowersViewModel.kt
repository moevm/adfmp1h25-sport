package com.khl_app.ui.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khl_app.domain.ApiService
import com.khl_app.domain.models.FollowerResponse
import com.khl_app.storage.models.TokenData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class FollowersViewModel(
    private val apiService: ApiService,
    private val tokenRepository: com.khl_app.domain.storage.IRepository<TokenData>
) : ViewModel() {

    private val _uiState = MutableStateFlow<FollowersUiState>(FollowersUiState.Loading)
    val uiState: StateFlow<FollowersUiState> = _uiState.asStateFlow()

    private val _followers = MutableStateFlow<List<FollowerResponse>>(emptyList())
    val followers: StateFlow<List<FollowerResponse>> = _followers.asStateFlow()

    private val _subscriptionState = MutableStateFlow<SubscriptionState>(SubscriptionState.Idle)
    val subscriptionState: StateFlow<SubscriptionState> = _subscriptionState.asStateFlow()

    private val _avatarState = MutableStateFlow<AvatarState>(AvatarState.Idle)
    val avatarState: StateFlow<AvatarState> = _avatarState.asStateFlow()

    init {
        loadFollowers()
    }

    fun loadFollowers() {
        viewModelScope.launch {
            _uiState.value = FollowersUiState.Loading
            try {
                tokenRepository.getInfo().collect { tokenData ->
                    val accessToken = tokenData.accessToken
                    if (accessToken.isNotEmpty()) {
                        val response = apiService.getFollowers("Bearer $accessToken")
                        if (response.isSuccessful && response.body() != null) {
                            val followersList = response.body()!!
                            _followers.value = followersList
                            _uiState.value = FollowersUiState.Success
                        } else {
                            _uiState.value =
                                FollowersUiState.Error("Ошибка загрузки: ${response.code()}")
                        }
                    } else {
                        _uiState.value = FollowersUiState.Error("Токен авторизации отсутствует")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = FollowersUiState.Error("Ошибка: ${e.message}")
            }
        }
    }

    fun removeFollower(followerId: String) {
        viewModelScope.launch {
            try {
                tokenRepository.getInfo().collect { tokenData ->
                    val accessToken = tokenData.accessToken
                    if (accessToken.isNotEmpty()) {
                        val response = apiService.unsubscribe("Bearer $accessToken", followerId)
                        if (response.isSuccessful) {
                            _subscriptionState.value = SubscriptionState.Success("Успешно отписались")
                            loadFollowers() // Обновляем список подписчиков
                        } else {
                            _subscriptionState.value = SubscriptionState.Error("Ошибка отписки: ${response.code()}")
                        }
                    } else {
                        _subscriptionState.value = SubscriptionState.Error("Токен авторизации отсутствует")
                    }
                }
            } catch (e: Exception) {
                _subscriptionState.value = SubscriptionState.Error("Ошибка: ${e.message}")
            }
        }
    }

    fun subscribeToUser(userId: String) {
        viewModelScope.launch {
            _subscriptionState.value = SubscriptionState.Loading
            try {
                tokenRepository.getInfo().collect { tokenData ->
                    val accessToken = tokenData.accessToken
                    if (accessToken.isNotEmpty()) {
                        val response = apiService.subscribe("Bearer $accessToken", userId)
                        if (response.isSuccessful) {
                            _subscriptionState.value = SubscriptionState.Success("Успешно подписались")
                            loadFollowers() // Обновляем список подписчиков
                        } else {
                            _subscriptionState.value = SubscriptionState.Error("Ошибка подписки: ${response.code()}")
                        }
                    } else {
                        _subscriptionState.value = SubscriptionState.Error("Токен авторизации отсутствует")
                    }
                }
            } catch (e: Exception) {
                _subscriptionState.value = SubscriptionState.Error("Ошибка: ${e.message}")
            }
        }
    }

    // Метод для сжатия изображения до 100x100
    private fun compressBitmap(bitmap: Bitmap): String {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true)
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // Метод для установки аватара
    fun setAvatar(bitmap: Bitmap) {
        viewModelScope.launch {
            _avatarState.value = AvatarState.Loading
            try {
                tokenRepository.getInfo().collect { tokenData ->
                    val accessToken = tokenData.accessToken
                    if (accessToken.isNotEmpty()) {
                        // Сжимаем и конвертируем изображение в base64
                        val base64Avatar = compressBitmap(bitmap)

                        // Отправляем на сервер
                        val response = apiService.setAvatar("Bearer $accessToken", base64Avatar)
                        if (response.isSuccessful) {
                            _avatarState.value = AvatarState.Success("Аватар успешно установлен")
                            loadFollowers() // Обновляем список, чтобы получить свой обновленный аватар
                        } else {
                            _avatarState.value = AvatarState.Error("Ошибка установки аватара: ${response.code()}")
                        }
                    } else {
                        _avatarState.value = AvatarState.Error("Токен авторизации отсутствует")
                    }
                }
            } catch (e: Exception) {
                _avatarState.value = AvatarState.Error("Ошибка: ${e.message}")
            }
        }
    }

    fun resetSubscriptionState() {
        _subscriptionState.value = SubscriptionState.Idle
    }

    fun resetAvatarState() {
        _avatarState.value = AvatarState.Idle
    }
}

sealed class FollowersUiState {
    object Loading : FollowersUiState()
    object Success : FollowersUiState()
    data class Error(val message: String) : FollowersUiState()
}

sealed class SubscriptionState {
    object Idle : SubscriptionState()
    object Loading : SubscriptionState()
    data class Success(val message: String) : SubscriptionState()
    data class Error(val message: String) : SubscriptionState()
}

sealed class AvatarState {
    object Idle : AvatarState()
    object Loading : AvatarState()
    data class Success(val message: String) : AvatarState()
    data class Error(val message: String) : AvatarState()
}