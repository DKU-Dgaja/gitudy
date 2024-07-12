package com.takseha.presentation.viewmodel.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.TokenManager
import com.takseha.data.repository.auth.GitudyAuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var tokenManager: TokenManager

    private var _loginPageUrl = MutableLiveData<String>()
    val loginPageUrl : LiveData<String>
        get() = _loginPageUrl

    fun startLogin(platformType: String) = viewModelScope.launch {
        tokenManager = TokenManager(getApplication())

        val loginPageInfos = tokenManager.getLoginPages()

        if (loginPageInfos != null) {
            _loginPageUrl.value = when (platformType) {
                "KAKAO" -> loginPageInfos.filter { it.platformType == "KAKAO" }
                    .joinToString("") { it.url }

                "GOOGLE" -> loginPageInfos.filter { it.platformType == "GOOGLE" }
                    .joinToString("") { it.url }

                "GITHUB" -> loginPageInfos.filter { it.platformType == "GITHUB" }
                    .joinToString("") { it.url }

                else -> null
            }
            Log.d("LoginViewModel", "url: ${loginPageUrl.value}")
        } else {
            Log.e("LoginViewModel", "loginPageInfos is null")
        }
    }
}