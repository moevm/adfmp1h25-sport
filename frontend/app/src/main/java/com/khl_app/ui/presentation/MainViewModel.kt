package com.khl_app.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khl_app.domain.ApiClient
import com.khl_app.domain.storage.IRepository
import com.khl_app.storage.models.TeamData
import com.khl_app.storage.models.TokenData
import kotlinx.coroutines.launch


class MainViewModel(
    private val tokenCache: IRepository<TokenData>,
    private val teamCache: IRepository<TeamData>,
) : ViewModel() {

    fun login(login: String, password: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response =
                    ApiClient.apiService.login(mapOf("login" to login, "password" to password))
                if (response.isSuccessful && response.body() != null) {
                    tokenCache.saveInfo(TokenData(response.body()!!.accessToken, response.body()!!.refreshToken))
                    onResult(null)
                } else {
                    onResult("Something wrong with response")
                }
            } catch (e: Exception) {
                onResult(e.message)
            }
        }
    }

    fun register(login: String, password: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response =
                    ApiClient.apiService.register(mapOf(login to "login", password to "password"))
                if (response.isSuccessful && response.body() != null) {
                    tokenCache.saveInfo(TokenData(response.body()!!.accessToken, response.body()!!.refreshToken))
                    onResult(null)
                } else {
                    onResult("Something wrong with response")
                }
            } catch (e: Exception) {
                onResult(e.message)
            }
        }
    }
}