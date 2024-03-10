package com.takseha.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.takseha.common.model.SPKey
import com.takseha.common.util.SP
import com.takseha.data.dto.Role
import com.takseha.data.repository.GitudyRepository
import kotlinx.coroutines.launch

// viewModel에서 context 참조 필요한 경우 AndroidViewModel(application) 상속!
class LoginWebViewViewModel(application: Application) : AndroidViewModel(application) {
    private val gitudyRepository = GitudyRepository()
    private lateinit var prefs: SP

    private var _role = MutableLiveData<String>()
    val role : LiveData<String>
        get() = _role

    fun saveAllTokens(platformType: String, code: String, state: String) = viewModelScope.launch {
        prefs = SP(getApplication())

        val tokenResponse = gitudyRepository.getLoginTokens(platformType, code, state)
        val resCode = tokenResponse.resCode
        val resMsg = tokenResponse.resMsg
        val allTokens = tokenResponse.tokenInfo

        Log.d("GetTokenViewModel", "https status: $resCode, $resMsg")

        if (resCode == 200 && resMsg == "OK") {
            prefs.savePref(
                SPKey.ACCESS_TOKEN,
                allTokens.accessToken
            )
            prefs.savePref(
                SPKey.REFRESH_TOKEN,
                allTokens.refreshToken
            )
            _role.value = allTokens.role

            Log.d("GetTokenViewModel", "shared pref 저장된 access token: ${prefs.loadPref(SPKey.ACCESS_TOKEN, "0")}\n"
                    + "shared pref 저장된 refresh token: ${prefs.loadPref(SPKey.REFRESH_TOKEN, "0")}")
        } else {
            Log.e("GetTokenViewModel", "https status error: $resCode, $resMsg")
        }
    }
}