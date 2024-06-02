package com.takseha.presentation.viewmodel.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.takseha.common.model.SPKey
import com.takseha.common.util.SP
import com.takseha.data.repository.auth.GitudyAuthRepository
import kotlinx.coroutines.launch

// viewModel에서 context 참조 필요한 경우 AndroidViewModel(application) 상속!
class LoginWebViewViewModel(application: Application) : AndroidViewModel(application) {
    private val gitudyAuthRepository = GitudyAuthRepository()
    private lateinit var prefs: SP

    private var _role = MutableLiveData<String>()
    val role : LiveData<String>
        get() = _role

    fun saveAllTokens(platformType: String, code: String, state: String) = viewModelScope.launch {
        prefs = SP(getApplication())

        val tokenResponse = gitudyAuthRepository.getLoginTokens(platformType, code, state)

        if (tokenResponse.isSuccessful) {
            val resCode = tokenResponse.body()!!.resCode
            val resMsg = tokenResponse.body()!!.resMsg
            val allTokens = tokenResponse.body()!!.tokenInfo

            if (resCode == 200 && resMsg == "OK") {
                prefs.savePref(
                    SPKey.ACCESS_TOKEN,
                    allTokens.accessToken
                )
                prefs.savePref(
                    SPKey.REFRESH_TOKEN,
                    allTokens.refreshToken
                )
                _role.value = allTokens.role
                Log.d("LoginWebViewViewModel", "role: ${role.value}")
                Log.d("LoginWebViewViewModel", "https status: $resCode, $resMsg")
                Log.d("LoginWebViewViewModel", "Bearer token: ${prefs.loadPref(SPKey.ACCESS_TOKEN, "0")} ${prefs.loadPref(SPKey.REFRESH_TOKEN, "0")}")
            } else {
                Log.e("LoginWebViewViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e("LoginWebViewViewModel", "status: ${tokenResponse.code()}, message: ${tokenResponse.message()}")
        }
    }
}