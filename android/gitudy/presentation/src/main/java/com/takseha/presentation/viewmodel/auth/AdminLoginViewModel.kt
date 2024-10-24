package com.takseha.presentation.viewmodel.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.auth.login.AdminLoginRequest
import com.takseha.data.dto.auth.login.RoleStatus
import com.takseha.data.token.TokenManager
import com.takseha.presentation.viewmodel.common.BaseApplicationViewModel
import com.takseha.presentation.viewmodel.feed.StudyMainInfoState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminLoginViewModel(application: Application) : BaseApplicationViewModel(application) {
    private lateinit var tokenManager: TokenManager

    private val _loginState = MutableStateFlow<Boolean?>(null)
    val loginState = _loginState.asStateFlow()

    fun getAdminTokens(id: String, password: String) = viewModelScope.launch {
        tokenManager = TokenManager(getApplication())
        val request = AdminLoginRequest(id = id, password = password)
        safeApiCall(
            apiCall = { tokenManager.getAdminTokens(request) },
            onSuccess = { response ->
                if (response != null) {
                    _loginState.value = true
                } else {
                    Log.e("AdminLoginViewModel", "admin login token 생성 실패")
                }
            },
            onError = { e, response ->
                super.handleDefaultError(e)
                super.resetSnackbarMessage()
                _loginState.value = false
                e?.let {
                    Log.e("AdminLoginViewModel", "Exception: ${it.message}")
                } ?: run {
                    response?.let {
                        Log.e("AdminLoginViewModel", "HTTP Error: $it")
                    }
                }
            }
        )
    }
}