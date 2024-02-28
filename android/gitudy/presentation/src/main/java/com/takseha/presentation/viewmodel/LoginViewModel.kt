package com.takseha.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.repository.GitudyRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val gitudyRepository = GitudyRepository()

    private var _loginPageUrl = MutableLiveData<String>()
    val loginPageUrl : LiveData<String>
        get() = _loginPageUrl

    fun startLogin(platformType: String) = viewModelScope.launch {
        val loginResponse = gitudyRepository.getLoginPage()
        val resCode = loginResponse.resCode
        val resMsg = loginResponse.resMsg
        val loginPages = loginResponse.resObj

        Log.d("LoginViewModel", "https status: $resCode, $resMsg")

        if (resCode == 200 && resMsg == "OK") {
            _loginPageUrl.value = when (platformType) {
                "KAKAO" -> loginPages.filter { it.platformType == "KAKAO" }.map { it.url }.joinToString("")
                "GOOGLE" -> loginPages.filter { it.platformType == "GOOGLE" }.map { it.url }.joinToString("")
                "GITHUB" -> loginPages.filter { it.platformType == "GITHUB" }.map { it.url }.joinToString("")
                else -> null
            }
            Log.d("LoginViewModel", "url: ${loginPageUrl.value}")
        } else {
            Log.e("LoginViewModel", "https status error: $resCode, $resMsg")
        }
    }
}