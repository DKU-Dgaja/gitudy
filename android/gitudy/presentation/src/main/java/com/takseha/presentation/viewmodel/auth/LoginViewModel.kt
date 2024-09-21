package com.takseha.presentation.viewmodel.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.token.TokenManager
import com.takseha.presentation.viewmodel.common.BaseApplicationViewModel
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : BaseApplicationViewModel(application) {
    private lateinit var tokenManager: TokenManager

    private var _loginPageUrl = MutableLiveData<String>()
    val loginPageUrl: LiveData<String>
        get() = _loginPageUrl

    fun startLogin(platformType: String) = viewModelScope.launch {
        tokenManager = TokenManager(getApplication())
        safeApiCall(
            apiCall = { tokenManager.getLoginPages() },
            onSuccess = { response ->
                if (response != null) {
                    _loginPageUrl.value = when (platformType) {
                        "KAKAO" -> response.filter { it.platformType == "KAKAO" }
                            .joinToString("") { it.url }

                        "GOOGLE" -> response.filter { it.platformType == "GOOGLE" }
                            .joinToString("") { it.url }

                        "GITHUB" -> response.filter { it.platformType == "GITHUB" }
                            .joinToString("") { it.url }

                        else -> null
                    }
                    Log.d("LoginViewModel", "url: ${loginPageUrl.value}")
                }
            },
            onError = { e, response ->
                super.handleDefaultError(e)
                super.resetSnackbarMessage()
                e?.let {
                    Log.e("LoginViewModel", "Exception: ${it.message}")
                } ?: run {
                    response?.let {
                        Log.e("LoginViewModel", "HTTP Error: $it")
                    }
                }
            }
        )
    }
}
