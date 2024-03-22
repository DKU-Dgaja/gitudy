package com.takseha.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.common.model.SPKey
import com.takseha.common.util.SP
import com.takseha.data.repository.GitudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileHomeViewModel(application: Application) : AndroidViewModel(application) {
    private var gitudyRepository: GitudyRepository = GitudyRepository()
    private val prefs = SP(getApplication())

    private val bearerToken = "Bearer ${prefs.loadPref(SPKey.ACCESS_TOKEN, "0")} ${
        prefs.loadPref(SPKey.REFRESH_TOKEN, "0")}"

    private val _uiState = MutableStateFlow(ProfileUserInfoUiState())
    val uiState = _uiState.asStateFlow()

    fun getUserInfo() = viewModelScope.launch {
        val userInfoResponse = gitudyRepository.getUserInfo(bearerToken)

        if (userInfoResponse.isSuccessful) {
            val resCode = userInfoResponse.body()!!.resCode
            val resMsg = userInfoResponse.body()!!.resMsg
            val userInfo = userInfoResponse.body()!!.userInfo

            if (resCode == 200 && resMsg == "OK") {
                _uiState.update { it.copy(name = userInfo.name, githubId = userInfo.githubId, profileImgUrl = userInfo.profileImageUrl) }
            } else {
                Log.e("MainHomeViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e("MainHomeViewModel", "tokenResponse status: ${userInfoResponse.code()}\ntokenResponse message: ${userInfoResponse.message()}")
        }
    }
}

data class ProfileUserInfoUiState(
    var name: String = "",
    var githubId: String = "",
    var profileImgUrl: String = ""
)