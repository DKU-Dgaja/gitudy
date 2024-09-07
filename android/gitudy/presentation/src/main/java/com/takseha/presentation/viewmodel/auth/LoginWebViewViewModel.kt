package com.takseha.presentation.viewmodel.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.auth.login.RoleStatus
import com.takseha.data.token.TokenManager
import com.takseha.presentation.viewmodel.common.BaseApplicationViewModel
import kotlinx.coroutines.launch

// viewModel에서 context 참조 필요한 경우 AndroidViewModel(application) 상속!
class LoginWebViewViewModel(application: Application) : BaseApplicationViewModel(application) {
    private lateinit var tokenManager: TokenManager

    private var _role = MutableLiveData<RoleStatus>()
    val role : LiveData<RoleStatus>
        get() = _role

    fun saveAllTokens(platformType: String, code: String, state: String) = viewModelScope.launch {
        tokenManager = TokenManager(getApplication())
        safeApiCall(
            apiCall = { tokenManager.getLoginTokens(platformType, code, state) },
            onSuccess = { response ->
                if (response != null) {
                    _role.value = response.role
                    Log.d("LoginWebViewViewModel", "access token: ${response.accessToken}\nrefresh token: ${response.refreshToken}\nrole: ${role.value}")
                }
            },
            onError = { e, response ->
                super.handleDefaultError(e)
                super.resetSnackbarMessage()
                e?.let {
                    Log.e("LoginWebViewViewModel", "Exception: ${it.message}")
                } ?: run {
                    response?.let {
                        Log.e("LoginWebViewViewModel", "HTTP Error: $it")
                    }
                }
            }
        )
    }
}