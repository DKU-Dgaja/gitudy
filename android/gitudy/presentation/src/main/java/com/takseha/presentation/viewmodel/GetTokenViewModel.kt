package com.takseha.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.repository.GitudyRepository
import kotlinx.coroutines.launch

class GetTokenViewModel : ViewModel() {
    private val gitudyRepository = GitudyRepository()

    private var _accessToken = MutableLiveData<String>()
    val accessToken : LiveData<String>
        get() = _accessToken

    private var _refreshToken = MutableLiveData<String>()
    val refreshToken : LiveData<String>
        get() = _refreshToken

    fun getAllTokens(platformType: String, code: String, state: String) = viewModelScope.launch {
        val tokenResponse = gitudyRepository.getAllTokens(platformType, code, state)
        val resCode = tokenResponse.resCode
        val resMsg = tokenResponse.resMsg
        val allTokens = tokenResponse.tokenInfo

        Log.d("GetTokenViewModel", "https status: $resCode, $resMsg")

        if (resCode == 200 && resMsg == "OK") {
            _accessToken.value = allTokens.accessToken
            _refreshToken.value = allTokens.refreshToken
            Log.d("GetTokenViewModel", "access token: ${accessToken.value}\nrefresh token: ${refreshToken.value}")
        } else {
            Log.e("GetTokenViewModel", "https status error: $resCode, $resMsg")
        }
    }
}