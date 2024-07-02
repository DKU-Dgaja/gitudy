package com.takseha.presentation.viewmodel.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.takseha.data.repository.auth.GitudyAuthRepository

class SplashViewModel: ViewModel() {
    private var gitudyAuthRepository: GitudyAuthRepository = GitudyAuthRepository()

    private var _availableTokenCheck = MutableLiveData<Boolean>()
    val availableTokenCheck : LiveData<Boolean>
        get() = _availableTokenCheck

    suspend fun checkAvailableToken() {
        val checkTokenResponse = gitudyAuthRepository.getUserInfo()

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
                "checkTokenResponse status: ${checkTokenResponse.code()}\ncheckTokenResponse message: ${checkTokenResponse.message()}"
            )
        }
    }
}