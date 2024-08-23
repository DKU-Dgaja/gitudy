package com.takseha.presentation.viewmodel.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.auth.auth.UserInfoResponse
import com.takseha.data.dto.mystudy.SocialInfo
import com.takseha.data.dto.profile.Bookmark
import com.takseha.data.repository.gitudy.GitudyAuthRepository
import com.takseha.data.repository.gitudy.GitudyBookmarksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileHomeViewModel : ViewModel() {
    private var gitudyAuthRepository = GitudyAuthRepository()
    private var gitudyBookmarksRepository = GitudyBookmarksRepository()

    private val _userUiState = MutableStateFlow(ProfileInfoUiState())
    val userUiState = _userUiState.asStateFlow()

    private var _bookmarksState = MutableStateFlow(BookmarksUiState())
    val bookmarksState = _bookmarksState.asStateFlow()

    private var _bookmarkCursorIdxRes = MutableLiveData<Long?>()
    val bookmarkCursorIdxRes: LiveData<Long?>
        get() = _bookmarkCursorIdxRes

    fun getUserProfileInfo() = viewModelScope.launch {
        val userProfileInfoResponse = gitudyAuthRepository.getUserInfoUpdatePage()

        if (userProfileInfoResponse.isSuccessful) {
            val userProfileInfo = userProfileInfoResponse.body()!!
            val userGithubId = getUserInfo()?.githubId ?: ""
            val userPushAlarmYn = getUserInfo()?.pushAlarmYn ?: false
            _userUiState.update {
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

    fun getBookmarks(cursorIdx: Long?, limit: Long) = viewModelScope.launch {
        val bookmarksResponse = gitudyBookmarksRepository.getBookmarks(
            cursorIdx,
            limit
        )
        if (bookmarksResponse.isSuccessful) {
            val bookmarksInfo = bookmarksResponse.body()!!

            _bookmarkCursorIdxRes.value = bookmarksInfo.cursorIdx

            if (bookmarksInfo.bookmarkInfoList.isEmpty()) {
                _bookmarksState.update {
                    it.copy(
                        bookmarksInfo = bookmarksInfo.bookmarkInfoList,
                        isBookmarksEmpty = true,
                    )
                }
            } else {
                _bookmarksState.update { it.copy(
                    bookmarksInfo = bookmarksInfo.bookmarkInfoList,
                    isBookmarksEmpty = false
                ) }
            }
        } else {
            Log.e(
                "ProfileHomeViewModel",
                "bookmarksResponse status: ${bookmarksResponse.code()}\nbookmarksResponse message: ${bookmarksResponse.message()}"
            )
        }
    }

    suspend fun setBookmarkStatus(studyInfoId: Int) {
        val setBookmarkResponse = gitudyBookmarksRepository.setBookmarkStatus(
            studyInfoId
        )

        if (setBookmarkResponse.isSuccessful) {
            Log.d("ProfileHomeViewModel", setBookmarkResponse.code().toString())
        } else {
            Log.e(
                "ProfileHomeViewModel",
                "setBookmarkResponse status: ${setBookmarkResponse.code()}\nsetBookmarkResponse message: ${setBookmarkResponse.errorBody()?.string()}"
            )
        }
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

data class BookmarksUiState(
    var bookmarksInfo: List<Bookmark> = listOf(),
    var isBookmarksEmpty: Boolean = false
)