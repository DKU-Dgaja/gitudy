package com.takseha.presentation.viewmodel.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.Comment
import com.takseha.data.dto.mystudy.Commit
import com.takseha.data.dto.mystudy.CommitRejectRequest
import com.takseha.data.dto.mystudy.RepositoryInfo
import com.takseha.data.dto.mystudy.SocialInfo
import com.takseha.data.dto.profile.Bookmark
import com.takseha.data.dto.profile.CommitWithStudyName
import com.takseha.data.repository.gitudy.GitudyAuthRepository
import com.takseha.data.repository.gitudy.GitudyBookmarksRepository
import com.takseha.data.repository.gitudy.GitudyCommitsRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import com.takseha.presentation.viewmodel.common.BaseViewModel
import com.takseha.presentation.viewmodel.home.MyStudyWithTodo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyCommitViewModel : BaseViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()
    private var gitudyCommitsRepository = GitudyCommitsRepository()

    private val _uiState = MutableStateFlow(CommitListWithStudyNameState())
    val uiState = _uiState.asStateFlow()

    fun getMyCommitLists(studyInfoId: Int, cursorIdx: Long?, limit: Long) = viewModelScope.launch {
        safeApiCall(
            apiCall = { gitudyCommitsRepository.getMyCommitList(studyInfoId, cursorIdx, limit) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val commitList = response.body()?.commitInfoList!!
                    if (commitList.isEmpty()) {
                        _uiState.update {
                            it.copy(
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
                            _uiState.update {
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

data class StudyNameAndRepoState(
    val studyName: String = "",
    val studyRepo: RepositoryInfo? = null,
)

data class CommitListWithStudyNameState(
    val commitList: List<CommitWithStudyName> = listOf(),
    val isMyCommitEmpty: Boolean? = null
)