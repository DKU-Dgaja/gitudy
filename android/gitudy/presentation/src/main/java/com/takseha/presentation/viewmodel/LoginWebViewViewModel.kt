package com.takseha.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.common.model.SharedPreferencesKey
import com.takseha.common.util.SharedPreferences
import com.takseha.data.repository.GitudyRepository
import kotlinx.coroutines.launch

// viewModel에서 context 참조 필요한 경우 AndroidViewModel(application) 상속!
class LoginWebViewViewModel(application: Application) : AndroidViewModel(application) {
    private val gitudyRepository = GitudyRepository()
    private lateinit var prefs: SharedPreferences

    fun saveAllTokens(platformType: String, code: String, state: String) = viewModelScope.launch {
        prefs = SharedPreferences(getApplication())

        val tokenResponse = gitudyRepository.getAllTokens(platformType, code, state)
        val resCode = tokenResponse.resCode
        val resMsg = tokenResponse.resMsg
        val allTokens = tokenResponse.tokenInfo

        Log.d("GetTokenViewModel", "https status: $resCode, $resMsg")

        if (resCode == 200 && resMsg == "OK") {
            prefs.savePref(
                SharedPreferencesKey.ACCESS_TOKEN,
                allTokens.accessToken
            )
            prefs.savePref(
                SharedPreferencesKey.ACCESS_TOKEN,
                allTokens.refreshToken
            )
            Log.d("GetTokenViewModel", "shared pref 저장된 access token: ${prefs.loadPref(SharedPreferencesKey.ACCESS_TOKEN, "0")}\nshared pref 저장된 refresh token: ${prefs.loadPref(SharedPreferencesKey.REFRESH_TOKEN, "0")}")
        } else {
            Log.e("GetTokenViewModel", "https status error: $resCode, $resMsg")
        }
    }
}