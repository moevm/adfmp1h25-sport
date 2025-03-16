package com.khl_app.ui.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khl_app.domain.ApiService
import com.khl_app.domain.models.FollowerResponse
import com.khl_app.storage.models.TokenData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FollowersViewModel (
    private val apiService: ApiService,
    private val tokenRepository: com.khl_app.domain.storage.IRepository<TokenData>
) : ViewModel() {

    private val _uiState = MutableStateFlow<FollowersUiState>(FollowersUiState.Loading)
    val uiState: StateFlow<FollowersUiState> = _uiState.asStateFlow()

    private val _followers = MutableStateFlow<List<FollowerResponse>>(emptyList())
    val followers: StateFlow<List<FollowerResponse>> = _followers.asStateFlow()

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
        // Здесь должна быть логика для удаления подписчика через API
        // Пока просто имитируем удаление из локального списка
        _followers.value = _followers.value.filter { it.id != followerId }
    }
}

sealed class FollowersUiState {
    object Loading : FollowersUiState()
    object Success : FollowersUiState()
    data class Error(val message: String) : FollowersUiState()
}