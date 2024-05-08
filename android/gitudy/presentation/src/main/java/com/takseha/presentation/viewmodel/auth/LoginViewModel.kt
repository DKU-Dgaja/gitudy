package com.takseha.presentation.viewmodel.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.repository.auth.GitudyAuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val gitudyAuthRepository = GitudyAuthRepository()

    private var _loginPageUrl = MutableLiveData<String>()
    val loginPageUrl : LiveData<String>
        get() = _loginPageUrl

    fun startLogin(platformType: String) = viewModelScope.launch {
        val loginResponse = gitudyAuthRepository.getLoginPage()

        if (loginResponse.isSuccessful) {
            val resCode = loginResponse.body()!!.resCode
            val resMsg = loginResponse.body()!!.resMsg
            val loginPages = loginResponse.body()!!.loginPageInfos

            if (resCode == 200 && resMsg == "OK") {
                _loginPageUrl.value = when (platformType) {
                    "KAKAO" -> loginPages.filter { it.platformType == "KAKAO" }
                        .joinToString("") { it.url }

                    "GOOGLE" -> loginPages.filter { it.platformType == "GOOGLE" }
                        .joinToString("") { it.url }

                    "GITHUB" -> loginPages.filter { it.platformType == "GITHUB" }
                        .joinToString("") { it.url }

                    else -> null
                }
                Log.d("LoginViewModel", "https status: $resCode, $resMsg")
                Log.d("LoginViewModel", "url: ${loginPageUrl.value}")
            } else {
                Log.e("LoginViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e("LoginViewModel", "status: ${loginResponse.code()}, message: ${loginResponse.message()}")
        }
    }
}