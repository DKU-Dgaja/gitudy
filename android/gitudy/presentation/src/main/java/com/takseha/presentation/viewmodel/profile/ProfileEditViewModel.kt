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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.Serializable

class ProfileEditViewModel : ViewModel() {
    private var gitudyAuthRepository = GitudyAuthRepository()

    private val _uiState = MutableStateFlow(UserInfoUpdatePageResponse())
    val uiState = _uiState.asStateFlow()

    private var _isCorrectName = MutableLiveData<Boolean>()
    val isCorrectName : LiveData<Boolean>
        get() = _isCorrectName

    suspend fun getUserProfileInfo() {
        val userProfileInfoResponse = gitudyAuthRepository.getUserInfoUpdatePage()

        if (userProfileInfoResponse.isSuccessful) {
            val userProfileInfo = userProfileInfoResponse.body()!!
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
                "userProfileInfoResponse status: ${userProfileInfoResponse.code()}\nuserProfileInfoResponse message: ${userProfileInfoResponse.errorBody()?.string()}"
            )
        }
    }

    suspend fun checkNickname(name: String) {
        val request = CheckNicknameRequest(name)
        val correctNameResponse = gitudyAuthRepository.checkCorrectNickname(request)

        if (correctNameResponse.isSuccessful) {
            _isCorrectName.value = true
        } else {
            _isCorrectName.value = false
            Log.e("ProfileEditViewModel", "correctNameResponse status: ${correctNameResponse.code()}\ncorrectNameResponse message: ${correctNameResponse.errorBody()?.string()}")

        }
    }

    fun updateUserInfo(name: String, profileImageUrl: String, socialInfo: SocialInfo?, profilePublicYn: Boolean) = viewModelScope.launch {
        val request = UserInfoUpdateRequest(
            name = name,
            profileImageUrl = profileImageUrl,
            profilePublicYn = profilePublicYn,
            socialInfo = socialInfo
        )
        val userInfoUpdateResponse = gitudyAuthRepository.updateUserInfo(request)

        if (userInfoUpdateResponse.isSuccessful) {
            Log.d("ProfileEditViewModel", userInfoUpdateResponse.code().toString())
        } else {
            Log.e(
                "ProfileEditViewModel",
                "userInfoUpdateResponse status: ${userInfoUpdateResponse.code()}\nuserInfoUpdateResponse message: ${userInfoUpdateResponse.errorBody()?.string()}"
            )
        }
    }
}