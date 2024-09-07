package com.takseha.presentation.viewmodel.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.auth.register.CheckNicknameRequest
import com.takseha.data.dto.auth.register.RegisterRequest
import com.takseha.data.repository.github.GithubRepository
import com.takseha.data.repository.gitudy.GitudyAuthRepository
import com.takseha.data.token.TokenManager
import com.takseha.presentation.viewmodel.common.BaseApplicationViewModel
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : BaseApplicationViewModel(application) {
    private lateinit var gitudyAuthRepository: GitudyAuthRepository
    private lateinit var githubRepository: GithubRepository
    private lateinit var tokenManager: TokenManager

    private val _registerInfoState = MutableStateFlow(RegisterRequest())
    val registerInfoState = _registerInfoState.asStateFlow()

    private val _isCorrectName = MutableStateFlow<Boolean?>(null)
    val isCorrectName = _isCorrectName.asStateFlow()

    suspend fun checkNickname(name: String) {
        gitudyAuthRepository = GitudyAuthRepository()
        val request = CheckNicknameRequest(name)
        safeApiCall(
            apiCall = { gitudyAuthRepository.checkCorrectNickname(request) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    _isCorrectName.value = true
                }
            },
            onError = { e, response ->
                super.handleDefaultError(e)
                super.resetSnackbarMessage()
                _isCorrectName.value = false
                e?.let {
                    Log.e("RegisterViewModel", "Exception: ${it.message}")
                } ?: run {
                    response?.let {
                        Log.e("RegisterViewModel", "HTTP Error: $it")
                    }
                }
            }
        )
    }

    fun resetCorrectName() {
        _isCorrectName.value = null
    }


    fun setPushAlarmYn(isPush: Boolean) {
        _registerInfoState.update { it.copy(pushAlarmYn = isPush) }
    }
    fun setNickname(name: String) {
        _registerInfoState.update { it.copy(name = name) }
    }
    fun setFCMToken(fcmToken: String) {
        _registerInfoState.update { it.copy(fcmToken = fcmToken) }
    }

    fun getRegisterTokens() = viewModelScope.launch {
        tokenManager = TokenManager(getApplication())
        val request = registerInfoState.value
        safeApiCall(
            apiCall = { tokenManager.getRegisterTokens(request) },
            onSuccess = { response ->
                if (response != null) {
                    val role = response.role
                    Log.d("RegisterViewModel", "role changed: $role")
                } else {
                    Log.e("RegisterViewModel", "register token 생성 실패")
                }
            }
        )
    }
}