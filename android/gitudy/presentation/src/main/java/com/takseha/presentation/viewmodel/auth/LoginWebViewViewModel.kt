package com.takseha.presentation.viewmodel.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.auth.login.RoleStatus
import com.takseha.data.token.TokenManager
import kotlinx.coroutines.launch

// viewModel에서 context 참조 필요한 경우 AndroidViewModel(application) 상속!
class LoginWebViewViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var tokenManager: TokenManager

    private var _role = MutableLiveData<RoleStatus>()
    val role : LiveData<RoleStatus>
        get() = _role

    fun saveAllTokens(platformType: String, code: String, state: String) = viewModelScope.launch {
        tokenManager = TokenManager(getApplication())

        val tokenResponse = tokenManager.getLoginTokens(platformType, code, state)

        if (tokenResponse != null) {
            _role.value = tokenResponse.role
            Log.d("LoginWebViewViewModel", "access token: ${tokenResponse.accessToken}\nrefresh token: ${tokenResponse.refreshToken}\nrole: ${role.value}")
        } else {
            Log.e("LoginWebViewViewModel", "login token 생성 실패")
        }
    }
}