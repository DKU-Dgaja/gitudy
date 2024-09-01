package com.takseha.presentation.viewmodel.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.SocialInfo
import com.takseha.data.dto.profile.Bookmark
import com.takseha.data.repository.gitudy.GitudyAuthRepository
import com.takseha.data.repository.gitudy.GitudyBookmarksRepository
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileHomeViewModel : BaseViewModel() {
    private var gitudyAuthRepository = GitudyAuthRepository()
    private var gitudyBookmarksRepository = GitudyBookmarksRepository()

    private val _userUiState = MutableStateFlow(ProfileInfoUiState())
    val userUiState = _userUiState.asStateFlow()

    private var _bookmarksState = MutableStateFlow(BookmarksUiState())
    val bookmarksState = _bookmarksState.asStateFlow()

    private var _bookmarkCursorIdxRes = MutableLiveData<Long?>()
    val bookmarkCursorIdxRes: LiveData<Long?>
        get() = _bookmarkCursorIdxRes

    suspend fun getUserProfileInfo() {
        safeApiCall(
            apiCall = { gitudyAuthRepository.getUserInfoUpdatePage() },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val userProfileInfo = response.body()!!

                    _userUiState.update {
                        it.copy(
                            name = userProfileInfo.name,
                            profileImageUrl = userProfileInfo.profileImageUrl,
                            profilePublicYn = userProfileInfo.profilePublicYn,
                            socialInfo = userProfileInfo.socialInfo
                        )
                    }
                } else {
                    Log.e(
                        "ProfileHomeViewModel",
                        "userProfileInfoResponse status: ${response.code()}, userProfileInfoResponse error message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }


    suspend fun getUserInfo() {
        safeApiCall(
            apiCall = { gitudyAuthRepository.getUserInfo() },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val userGithubInfo = response.body()!!
                    val userGithubId = userGithubInfo.githubId
                    val userPushAlarmYn = userGithubInfo.pushAlarmYn

                    _userUiState.update {
                        it.copy(
                            githubId = userGithubId,
                            pushAlarmYn = userPushAlarmYn,
                        )
                    }
                } else {
                    Log.e(
                        "ProfileHomeViewModel",
                        "userGithubInfoResponse status: ${response.code()}, userGithubInfoResponse error message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }


    suspend fun getBookmarks(cursorIdx: Long?, limit: Long) {
        safeApiCall(
            apiCall = {
                gitudyBookmarksRepository.getBookmarks(
                    cursorIdx,
                    limit
                )
            },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val bookmarksInfo = response.body()!!

                    _bookmarkCursorIdxRes.value = bookmarksInfo.cursorIdx

                    if (bookmarksInfo.bookmarkInfoList.isEmpty()) {
                        _bookmarksState.update {
                            it.copy(
                                bookmarksInfo = bookmarksInfo.bookmarkInfoList,
                                isBookmarksEmpty = true,
                            )
                        }
                    } else {
                        _bookmarksState.update {
                            it.copy(
                                bookmarksInfo = bookmarksInfo.bookmarkInfoList,
                                isBookmarksEmpty = false
                            )
                        }
                    }
                } else {
                    Log.e(
                        "ProfileHomeViewModel",
                        "bookmarksResponse status: ${response.code()}\nbookmarksResponse message: ${response.message()}"
                    )
                }
            }
        )
    }

    suspend fun setBookmarkStatus(studyInfoId: Int, cursorIdx: Long?, limit: Long) {
        safeApiCall(
            apiCall = {
                gitudyBookmarksRepository.setBookmarkStatus(
                    studyInfoId
                )
            },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    viewModelScope.launch {
                        getBookmarks(cursorIdx, limit)
                    }
                    Log.d("ProfileHomeViewModel", response.code().toString())
                } else {
                    Log.e(
                        "ProfileHomeViewModel",
                        "setBookmarkResponse status: ${response.code()}\nsetBookmarkResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
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