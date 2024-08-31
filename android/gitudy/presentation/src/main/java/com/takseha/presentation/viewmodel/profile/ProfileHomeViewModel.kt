package com.takseha.presentation.viewmodel.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.auth.auth.UserInfoResponse
import com.takseha.data.dto.mystudy.SocialInfo
import com.takseha.data.dto.profile.Bookmark
import com.takseha.data.repository.gitudy.GitudyAuthRepository
import com.takseha.data.repository.gitudy.GitudyBookmarksRepository
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.async
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

    fun getUserProfileInfo() = viewModelScope.launch {
        val result = safeApiResponse {
            val userProfileInfoResponse = async { gitudyAuthRepository.getUserInfoUpdatePage() }
            val userGithubInfo = async { getUserInfo() }
            Pair(userProfileInfoResponse.await(), userGithubInfo.await())
        }

        result?.let { (userProfileInfoResponse, userGithubInfo) ->
            if (userProfileInfoResponse.isSuccessful) {
                val userProfileInfo = userProfileInfoResponse.body()!!
                val userGithubId = userGithubInfo?.githubId ?: ""
                val userPushAlarmYn = userGithubInfo?.pushAlarmYn ?: false
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
                    "userProfileInfoResponse status ${userProfileInfoResponse.code()}, userProfileInfoResponse status ${userProfileInfoResponse.errorBody()?.string()}"
                )
            }
        } ?: Log.e("ProfileHomeViewModel", "API 호출 실패")
    }


    private suspend fun getUserInfo(): UserInfoResponse? {
        return try {
            val response = gitudyAuthRepository.getUserInfo()
            if (response.isSuccessful) {
                Log.d(
                    "ProfileHomeViewModel",
                    "userGithubIdResponse status: ${response.code()}\nuserGithubIdResponse message: ${response.errorBody()?.string()}"
                )
                response.body()
            } else {
                Log.e(
                    "ProfileHomeViewModel",
                    "userGithubIdResponse status: ${response.code()}\nuserGithubIdResponse message: ${response.errorBody()?.string()}"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("ProfileHomeViewModel", "Error fetching getUserInfo()", e)
            null
        }
    }


    fun getBookmarks(cursorIdx: Long?, limit: Long) = viewModelScope.launch {
        safeApiCall(
            apiCall = {
                gitudyBookmarksRepository.getBookmarks(
                cursorIdx,
                limit
            ) },
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
                        _bookmarksState.update { it.copy(
                            bookmarksInfo = bookmarksInfo.bookmarkInfoList,
                            isBookmarksEmpty = false
                        ) }
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

    suspend fun setBookmarkStatus(studyInfoId: Int) {
        safeApiCall(
            apiCall = { gitudyBookmarksRepository.setBookmarkStatus(
                studyInfoId
            ) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    getBookmarks(null, 3)
                    Log.d("ProfileHomeViewModel", response.code().toString())
                } else {
                    Log.e(
                        "ProfileHomeViewModel",
                        "setBookmarkResponse status: ${response.code()}\nsetBookmarkResponse message: ${response.errorBody()?.string()}"
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