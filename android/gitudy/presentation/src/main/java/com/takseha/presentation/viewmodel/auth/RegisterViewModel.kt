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
import com.takseha.data.repository.GithubRepository
import com.takseha.data.repository.GitudyRepository
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var gitudyRepository: GitudyRepository
    private lateinit var githubRepository: GithubRepository
    private val prefs = SP(getApplication())

    private var _isCorrectId = MutableLiveData<Boolean>()
    val isCorrectId : LiveData<Boolean>
        get() = _isCorrectId

    fun checkGithubId(githubId: String) = viewModelScope.launch {
        githubRepository = GithubRepository()

        val githubResponse = githubRepository.checkCorrectGithubId(githubId)

        if (githubResponse.isSuccessful) {
            _isCorrectId.value = true
        } else {
            _isCorrectId.value = false
            Log.e("RegisterViewModel", "githubResponse status: ${githubResponse.code()}\ngithubResponse message: ${githubResponse.message()}")
        }
    }

    fun getRegisterTokens() = viewModelScope.launch {
        gitudyRepository = GitudyRepository()

        val bearerToken = "Bearer ${prefs.loadPref(SPKey.ACCESS_TOKEN, "0")} ${
            prefs.loadPref(
                SPKey.REFRESH_TOKEN,
                "0"
            )
        }"
        Log.d("RegisterViewModel", bearerToken)
        val request = RegisterRequest(
            github_id = prefs.loadPref(SPKey.GITHUB_ID, "0"),
            name = prefs.loadPref(SPKey.GITUDY_NAME, "0")
        )

        val tokenResponse = gitudyRepository.getRegisterTokens(bearerToken, request)

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

                Log.d("RegisterViewModel", "https status: $resCode, $resMsg")
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