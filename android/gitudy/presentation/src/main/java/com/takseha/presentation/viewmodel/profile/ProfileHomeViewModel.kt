package com.takseha.presentation.viewmodel.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.RepositoryInfo
import com.takseha.data.dto.mystudy.SocialInfo
import com.takseha.data.dto.profile.Bookmark
import com.takseha.data.dto.profile.CommitWithStudyName
import com.takseha.data.repository.gitudy.GitudyAuthRepository
import com.takseha.data.repository.gitudy.GitudyBookmarksRepository
import com.takseha.data.repository.gitudy.GitudyCommitsRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileHomeViewModel : BaseViewModel() {
    private var gitudyAuthRepository = GitudyAuthRepository()
    private var gitudyBookmarksRepository = GitudyBookmarksRepository()
    private var gitudyStudyRepository = GitudyStudyRepository()
    private var gitudyCommitsRepository = GitudyCommitsRepository()

    private val _userUiState = MutableStateFlow(ProfileInfoUiState())
    val userUiState = _userUiState.asStateFlow()

    private var _bookmarksState = MutableStateFlow(BookmarksUiState())
    val bookmarksState = _bookmarksState.asStateFlow()

    private var _myCommitsState = MutableStateFlow(CommitListWithStudyNameState())
    val myCommitsState = _myCommitsState.asStateFlow()

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

    fun getMyCommitLists(studyInfoId: Int?, cursorIdx: Long?, limit: Long) = viewModelScope.launch {
        safeApiCall(
            apiCall = { gitudyCommitsRepository.getMyCommitList(studyInfoId, cursorIdx, limit) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val commitList = response.body()?.commitInfoList!!
                    if (commitList.isEmpty()) {
                        _myCommitsState.update {
                            it.copy(
                                commitList = emptyList(),
                                isMyCommitEmpty = true
                            )
                        }
                    } else {
                        viewModelScope.launch {
                            val commitListWithStudyName = commitList.map { commit ->
                                val studyNameAndRepo = getStudyNameAndRepoInfo(commit.studyInfoId)
                                CommitWithStudyName(
                                    studyName = studyNameAndRepo?.studyName ?: "",
                                    studyRepo = studyNameAndRepo?.studyRepo ?: RepositoryInfo(),
                                    commit = commit
                                )
                            }
                            _myCommitsState.update {
                                it.copy(
                                    commitList = commitListWithStudyName,
                                    isMyCommitEmpty = false
                                )
                            }
                        }
                    }
                } else {
                    Log.e(
                        "MyCommitViewModel",
                        "myCommitListResponse status: ${response.code()}\nmyCommitListResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }

    private suspend fun getStudyNameAndRepoInfo(studyInfoId: Int): StudyNameAndRepoState? {
        return try {
            val response = gitudyStudyRepository.getStudyInfo(
                studyInfoId
            )
            if (response.isSuccessful) {
                val studyInfo = response.body()!!
                StudyNameAndRepoState(
                    studyName = studyInfo.topic,
                    studyRepo = studyInfo.githubLinkInfo
                )
            } else {
                Log.e(
                    "MyCommitViewModel",
                    "studyNameAndRepoResponse status: ${response.code()}\nstudyNameAndRepoResponse message: ${
                        response.errorBody()?.string()
                    }"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("MyCommitViewModel", "Error fetching getStudyNameAndRepoInfo()", e)
            null
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

data class StudyNameAndRepoState(
    val studyName: String = "",
    val studyRepo: RepositoryInfo? = null,
)

data class CommitListWithStudyNameState(
    val commitList: List<CommitWithStudyName> = listOf(),
    val isMyCommitEmpty: Boolean = false
)