package com.takseha.presentation.viewmodel.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.takseha.common.model.SPKey
import com.takseha.common.util.SP
import com.takseha.data.repository.auth.GitudyAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SplashViewModel(application: Application) : AndroidViewModel(application) {
    private var gitudyAuthRepository: GitudyAuthRepository = GitudyAuthRepository()
    private val prefs = SP(getApplication())

    private val bearerToken = "Bearer ${prefs.loadPref(SPKey.ACCESS_TOKEN, "0")} ${
        prefs.loadPref(SPKey.REFRESH_TOKEN, "0")
    }"

//    private val _availableTokenState = MutableStateFlow<Boolean>(false)
//    val availableTokenState = _availableTokenState.asStateFlow()
    private var _availableTokenCheck = MutableLiveData<Boolean>()
    val availableTokenCheck : LiveData<Boolean>
        get() = _availableTokenCheck

    suspend fun checkAvailableToken() {
        val checkTokenResponse = gitudyAuthRepository.getUserInfo(bearerToken)

        if (checkTokenResponse.isSuccessful) {
            val resCode = checkTokenResponse.body()!!.resCode
            val resMsg = checkTokenResponse.body()!!.resMsg

            if (resCode == 200 && resMsg == "OK") {
                _availableTokenCheck.value = true
            } else {
                _availableTokenCheck.value = false
                Log.e("SplashViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e(
                "SplashViewModel",
                "tokenResponse status: ${checkTokenResponse.code()}\ntokenResponse message: ${checkTokenResponse.message()}"
            )
        }
    }
}