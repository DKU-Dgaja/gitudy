package com.takseha.presentation.viewmodel.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.takseha.common.model.SPKey
import com.takseha.common.util.SP
import com.takseha.data.dto.auth.register.RegisterRequest
import com.takseha.data.repository.auth.GithubRepository
import com.takseha.data.repository.auth.GitudyAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var gitudyAuthRepository: GitudyAuthRepository
    private lateinit var githubRepository: GithubRepository
    private val prefs = SP(getApplication())

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
        val resCode = correctNameResponse.body()!!.resCode
        val resMsg = correctNameResponse.body()!!.resMsg
        val resObj = correctNameResponse.body()!!.resObj

        if (correctNameResponse.isSuccessful) {
            if (resCode == 200 && resMsg == "OK") {
                _isCorrectName.value = true
                Log.d("RegisterViewModel", resObj)
            } else {
                _isCorrectName.value = false
            }
        } else {
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

    fun getRegisterTokens() = viewModelScope.launch {
        gitudyAuthRepository = GitudyAuthRepository()

        val bearerToken = "Bearer ${prefs.loadPref(SPKey.ACCESS_TOKEN, "0")} ${
            prefs.loadPref(
                SPKey.REFRESH_TOKEN,
                "0"
            )
        }"
        Log.d("RegisterViewModel", bearerToken)
        val request = registerInfoState.value

        val tokenResponse = gitudyAuthRepository.getRegisterTokens(bearerToken, request)

        if (tokenResponse.isSuccessful) {
            val resCode = tokenResponse.body()!!.resCode
            val resMsg = tokenResponse.body()!!.resMsg
            val allTokens = tokenResponse.body()!!.tokenInfo

            if (resCode == 200 && resMsg == "OK") {
                prefs.savePref(
                    SPKey.ACCESS_TOKEN,
                    allTokens.accessToken
                )
                prefs.savePref(
                    SPKey.REFRESH_TOKEN,
                    allTokens.refreshToken
                )
                Log.d(
                    "RegisterViewModel",
                    "shared pref 저장된 access token: ${prefs.loadPref(SPKey.ACCESS_TOKEN, "0")}\n"
                            + "shared pref 저장된 refresh token: ${
                        prefs.loadPref(
                            SPKey.REFRESH_TOKEN,
                            "0"
                        )
                    }"
                )
            } else {    // 추후에 status 에러 코드에 맞춰서 에러 상황 다르게 처리! 일단은 다 id 입력 오류라고 생각하고 처리
                Log.e("RegisterViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e("RegisterViewModel", "tokenResponse status: ${tokenResponse.code()}\ntokenResponse message: ${tokenResponse.message()}")
        }
    }
}