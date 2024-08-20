package com.takseha.presentation.viewmodel.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.takseha.data.repository.gitudy.GitudyAuthRepository
import com.takseha.data.token.TokenManager

class SettingHomeViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var gitudyAuthRepository: GitudyAuthRepository
    private lateinit var tokenManager: TokenManager

    suspend fun logout() {
        tokenManager = TokenManager(getApplication())
        val logoutResponse = tokenManager.logout()

        if (logoutResponse) {
            Log.d("SettingHomeViewModel", "logout 성공!\naccess token: ${tokenManager.accessToken}\nrefresh token: ${tokenManager.refreshToken}")
        } else {
            Log.e("SettingHomeViewModel", "logout 실패!")
        }
    }

    suspend fun deleteUserAccount() {
        tokenManager = TokenManager(getApplication())
        val deleteAccountResponse = tokenManager.deleteUserAccount()

        if (deleteAccountResponse) {
            Log.d("SettingHomeViewModel", "deleteAccount 성공!\naccess token: ${tokenManager.accessToken}\nrefresh token: ${tokenManager.refreshToken}")
        } else {
            Log.e("SettingHomeViewModel", "deleteAccount 실패!")
        }
    }
}