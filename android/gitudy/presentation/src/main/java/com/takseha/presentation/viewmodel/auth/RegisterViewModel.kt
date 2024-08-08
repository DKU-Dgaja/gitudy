package com.takseha.presentation.viewmodel.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.auth.register.RegisterRequest
import com.takseha.data.repository.github.GithubRepository
import com.takseha.data.repository.gitudy.GitudyAuthRepository
import com.takseha.data.token.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var gitudyAuthRepository: GitudyAuthRepository
    private lateinit var githubRepository: GithubRepository
    private lateinit var tokenManager: TokenManager

    private val _registerInfoState = MutableStateFlow(RegisterRequest())
    val registerInfoState = _registerInfoState.asStateFlow()

    private var _isCorrectId = MutableLiveData<Boolean>()
    val isCorrectId : LiveData<Boolean>
        get() = _isCorrectId

    private var _isCorrectName = MutableLiveData<Boolean>()
    val isCorrectName : LiveData<Boolean>
        get() = _isCorrectName

    suspend fun checkGithubId(githubId: String) {
        githubRepository = GithubRepository()

        val githubResponse = githubRepository.checkCorrectGithubId(githubId)

        if (githubResponse.isSuccessful) {
            _isCorrectId.value = true
        } else {
            _isCorrectId.value = false
            Log.e("RegisterViewModel", "githubResponse status: ${githubResponse.code()}\ngithubResponse message: ${githubResponse.message()}")
        }
    }

    suspend fun checkNickname(name: String) {
        gitudyAuthRepository = GitudyAuthRepository()

        val correctNameResponse = gitudyAuthRepository.checkCorrectNickname(name)

        if (correctNameResponse.isSuccessful) {
            _isCorrectName.value = true
        } else {
            _isCorrectName.value = false
            Log.e("RegisterViewModel", "correctNameResponse status: ${correctNameResponse.code()}\ncorrectNameResponse message: ${correctNameResponse.message()}")
        }
    }

    fun setPushAlarmYn(isPush: Boolean) {
        _registerInfoState.update { it.copy(pushAlarmYn = isPush) }
    }

    fun setNickname(name: String) {
        _registerInfoState.update { it.copy(name = name) }
    }
    fun setGithubId(githubId: String) {
        _registerInfoState.update { it.copy(githubId = githubId) }
    }
    fun setFCMToken(fcmToken: String) {
        _registerInfoState.update { it.copy(fcmToken = fcmToken) }
    }

    fun getRegisterTokens() = viewModelScope.launch {
        tokenManager = TokenManager(getApplication())

        val request = registerInfoState.value

        val registerResponse = tokenManager.getRegisterTokens(request)

        if (registerResponse != null) {
            val role = registerResponse.role
            Log.d("RegisterViewModel", "role changed: $role")
        } else {
            Log.e("RegisterViewModel", "register token 생성 실패")
        }
    }
}