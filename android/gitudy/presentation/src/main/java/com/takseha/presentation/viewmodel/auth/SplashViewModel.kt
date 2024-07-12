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
            _availableTokenCheck.value = true
        } else {
            _availableTokenCheck.value = false
            Log.e(
                "SplashViewModel",
                "checkTokenResponse status: ${checkTokenResponse.code()}\ncheckTokenResponse message: ${checkTokenResponse.message()}"
            )
        }
    }
}