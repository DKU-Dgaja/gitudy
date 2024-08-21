package com.takseha.presentation.viewmodel.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.auth.auth.UserInfoResponse
import com.takseha.data.dto.auth.auth.UserInfoUpdatePageResponse
import com.takseha.data.dto.feed.StudyCountResponse
import com.takseha.data.dto.feed.StudyInfo
import com.takseha.data.dto.mystudy.SocialInfo
import com.takseha.data.dto.mystudy.TodoProgressResponse
import com.takseha.data.repository.gitudy.GitudyAuthRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import com.takseha.presentation.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.Serializable

class ProfileHomeViewModel : ViewModel() {
    private var gitudyAuthRepository = GitudyAuthRepository()

    private val _uiState = MutableStateFlow(ProfileInfoUiState())
    val uiState = _uiState.asStateFlow()

    suspend fun getUserProfileInfo() {
        val userProfileInfoResponse = gitudyAuthRepository.getUserInfoUpdatePage()

        if (userProfileInfoResponse.isSuccessful) {
            val userProfileInfo = userProfileInfoResponse.body()!!
            val userGithubId = getUserInfo()?.githubId ?: ""
            val userPushAlarmYn = getUserInfo()?.pushAlarmYn ?: false
            _uiState.update {
                it.copy(
                    name = userProfileInfo.name,
                    githubId = userGithubId,
                    pushAlarmYn = userPushAlarmYn,
                    profileImageUrl = userProfileInfo.profileImageUrl,
                    profilePublicYn = userProfileInfo.profilePublicYn,
                    socialInfo = userProfileInfo.socialInfo
                )
            }
        } else {
            Log.e(
                "ProfileHomeViewModel",
                "userProfileInfoResponse status: ${userProfileInfoResponse.code()}\nuserProfileInfoResponse message: ${userProfileInfoResponse.errorBody()?.string()}"
            )
        }
    }

    private suspend fun getUserInfo(): UserInfoResponse? {
        val userGithubIdResponse = gitudyAuthRepository.getUserInfo()

        if (userGithubIdResponse.isSuccessful) {
            return userGithubIdResponse.body()!!
        } else {
            Log.e(
                "ProfileHomeViewModel",
                "userGithubIdResponse status: ${userGithubIdResponse.code()}\nuserGithubIdResponse message: ${userGithubIdResponse.errorBody()?.string()}"
            )
        }
        return null
    }
}

data class ProfileInfoUiState(
    val name: String = "",
    val githubId: String = "",
    val pushAlarmYn: Boolean? = null,
    val profileImageUrl: String = "",
    val profilePublicYn: Boolean? = null,
    val socialInfo: SocialInfo? = null
)