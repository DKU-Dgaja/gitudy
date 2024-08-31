package com.takseha.presentation.viewmodel.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.takseha.data.dto.feed.MessageRequest
import com.takseha.data.repository.gitudy.GitudyAuthRepository
import com.takseha.data.token.TokenManager
import com.takseha.presentation.viewmodel.common.BaseApplicationViewModel

class SettingHomeViewModel(application: Application) : BaseApplicationViewModel(application) {
    private lateinit var gitudyAuthRepository: GitudyAuthRepository
    private lateinit var tokenManager: TokenManager

    suspend fun updatePushAlarmYn(pushAlarmEnable: Boolean) {
        gitudyAuthRepository = GitudyAuthRepository()
        safeApiCall(
            apiCall = { gitudyAuthRepository.updatePushAlarmYn(pushAlarmEnable) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    Log.d("SettingHomeViewModel", response.code().toString())
                } else {
                    Log.e(
                        "SettingHomeViewModel",
                        "updatePushAlarmYnResponse status: ${response.code()}\nupdatePushAlarmYnResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }

    suspend fun logout() {
        tokenManager = TokenManager(getApplication())
        safeApiCall(
            apiCall = { tokenManager.logout() },
            onSuccess = { response ->
                if (response) {
                    Log.d("SettingHomeViewModel", "logout 성공!\naccess token: ${tokenManager.accessToken}\nrefresh token: ${tokenManager.refreshToken}")
                } else {
                    Log.e("SettingHomeViewModel", "logout 실패!")
                }
            }
        )
    }

    suspend fun deleteUserAccount(message: String) {
        tokenManager = TokenManager(getApplication())
        val request = MessageRequest(message)
        safeApiCall(
            apiCall = { tokenManager.deleteUserAccount(request) },
            onSuccess = { response ->
                if (response) {
                    Log.d("SettingHomeViewModel", "deleteAccount 성공!\naccess token: ${tokenManager.accessToken}\nrefresh token: ${tokenManager.refreshToken}")
                } else {
                    Log.e("SettingHomeViewModel", "deleteAccount 실패!")
                }
            }
        )
    }
}