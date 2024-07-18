package com.takseha.presentation.viewmodel.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.takseha.data.token.TokenManager
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var tokenManager: TokenManager

    private var _loginPageUrl = MutableLiveData<String>()
    val loginPageUrl : LiveData<String>
        get() = _loginPageUrl

    fun startLogin(platformType: String) = viewModelScope.launch {
        tokenManager = TokenManager(getApplication())

        val loginPageInfoList = tokenManager.getLoginPages()

        if (loginPageInfoList != null) {
            _loginPageUrl.value = when (platformType) {
                "KAKAO" -> loginPageInfoList.filter { it.platformType == "KAKAO" }
                    .joinToString("") { it.url }

                "GOOGLE" -> loginPageInfoList.filter { it.platformType == "GOOGLE" }
                    .joinToString("") { it.url }

                "GITHUB" -> loginPageInfoList.filter { it.platformType == "GITHUB" }
                    .joinToString("") { it.url }

                else -> null
            }
            Log.d("LoginViewModel", "url: ${loginPageUrl.value}")
        } else {
            Log.e("LoginViewModel", "loginPageInfoList is null")
        }
    }
}