package com.takseha.presentation.viewmodel.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.auth.auth.UserInfoUpdatePageResponse
import com.takseha.data.dto.auth.auth.UserInfoUpdateRequest
import com.takseha.data.dto.auth.register.CheckNicknameRequest
import com.takseha.data.dto.feed.StudyCountResponse
import com.takseha.data.dto.feed.StudyInfo
import com.takseha.data.dto.mystudy.SocialInfo
import com.takseha.data.dto.mystudy.TodoProgressResponse
import com.takseha.data.repository.gitudy.GitudyAuthRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import com.takseha.presentation.R
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.Serializable

class ProfileEditViewModel : BaseViewModel() {
    private var gitudyAuthRepository = GitudyAuthRepository()

    private val _uiState = MutableStateFlow(UserInfoUpdatePageResponse())
    val uiState = _uiState.asStateFlow()

    private val _isCorrectName = MutableStateFlow<Boolean?>(null)
    val isCorrectName = _isCorrectName.asStateFlow()

    fun getUserProfileInfo() =viewModelScope.launch {
        safeApiCall(
            apiCall = { gitudyAuthRepository.getUserInfoUpdatePage() },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val userProfileInfo = response.body()!!
                    _uiState.update {
                        it.copy(
                            name = userProfileInfo.name,
                            profileImageUrl = userProfileInfo.profileImageUrl,
                            profilePublicYn = userProfileInfo.profilePublicYn,
                            socialInfo = userProfileInfo.socialInfo
                        )
                    }
                } else {
                    Log.e(
                        "ProfileEditViewModel",
                        "userProfileInfoResponse status: ${response.code()}\nuserProfileInfoResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }

    suspend fun checkNickname(name: String) {
        val request = CheckNicknameRequest(name)
        safeApiCall(
            apiCall = { gitudyAuthRepository.checkCorrectNickname(request) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    _isCorrectName.value = true
                } else {
                    _isCorrectName.value = false
                    Log.e("ProfileEditViewModel", "correctNameResponse status: ${response.code()}\ncorrectNameResponse message: ${response.errorBody()?.string()}")

                }
            }
        )
    }

    fun resetCorrectName() {
        _isCorrectName.value = null
    }

    suspend fun updateUserInfo(name: String, profileImageUrl: String, socialInfo: SocialInfo?, profilePublicYn: Boolean) {
        val request = UserInfoUpdateRequest(
            name = name,
            profileImageUrl = profileImageUrl,
            profilePublicYn = profilePublicYn,
            socialInfo = socialInfo
        )
        safeApiCall(
            apiCall = { gitudyAuthRepository.updateUserInfo(request) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    Log.d("ProfileEditViewModel", response.code().toString())
                } else {
                    Log.e(
                        "ProfileEditViewModel",
                        "userInfoUpdateResponse status: ${response.code()}\nuserInfoUpdateResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }
}