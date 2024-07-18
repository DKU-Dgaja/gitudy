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
            if (checkTokenResponse.code() == 401 || checkTokenResponse.code() == 403) {
                // token 문제일 때
                _availableTokenCheck.value = false
            } else {
                // 다른 문제일 때
                // todo: 서버에 문제가 있습니다. 이런 식의 팝업창 띄우기
            }
            Log.e(
                "SplashViewModel",
                "checkTokenResponse status: ${checkTokenResponse.code()}\ncheckTokenResponse message: ${checkTokenResponse.errorBody()!!.string()}"
            )
        }
    }
}