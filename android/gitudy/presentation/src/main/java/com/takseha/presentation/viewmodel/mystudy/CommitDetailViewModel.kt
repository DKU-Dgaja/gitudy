package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.Comment
import com.takseha.data.dto.mystudy.CommitCommentRequest
import com.takseha.data.dto.mystudy.CommitRejectRequest
import com.takseha.data.dto.mystudy.RepositoryInfo
import com.takseha.data.repository.gitudy.GitudyCommitsRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CommitDetailViewModel : BaseViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()
    private var gitudyCommitsRepository = GitudyCommitsRepository()

    private val _repositoryInfoState = MutableStateFlow(RepositoryInfo())
    val repositoryInfoState = _repositoryInfoState.asStateFlow()

    private val _commentState = MutableStateFlow<List<Comment>?>(null)
    val commentState = _commentState.asStateFlow()

    suspend fun getRepositoryInfo(studyInfoId: Int) {
        safeApiCall(
            apiCall = { gitudyStudyRepository.getStudyInfo(studyInfoId) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val repoInfo = response.body()!!.githubLinkInfo
                    _repositoryInfoState.update {
                        it.copy(
                            owner = repoInfo.owner,
                            name = repoInfo.name,
                            branchName = repoInfo.branchName
                        )
                    }
                } else {
                    Log.e(
                        "CommitDetailViewModel",
                        "myStudyInfoResponse status: ${response.code()}\nmyStudyInfoResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }

    suspend fun approveCommit(studyInfoId: Int, commitId: Int) {
        safeApiCall(
            apiCall = { gitudyCommitsRepository.approveCommit(commitId, studyInfoId) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    Log.d("CommitDetailViewModel", response.code().toString())
                } else {
                    Log.e(
                        "CommitDetailViewModel",
                        "approveCommitResponse status: ${response.code()}\napproveCommitResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }

    suspend fun rejectCommit(studyInfoId: Int, rejectionReason: String, commitId: Int) {
        val request = CommitRejectRequest(rejectionReason = rejectionReason)
        safeApiCall(
            apiCall = { gitudyCommitsRepository.rejectCommit(commitId, studyInfoId, request) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    Log.d("CommitDetailViewModel", response.code().toString())
                } else {
                    Log.e(
                        "CommitDetailViewModel",
                        "rejectCommitResponse status: ${response.code()}\nrejectCommitResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            },
            onError = { e, response ->
                super.handleDefaultError(e)
                super.resetSnackbarMessage()
                e?.let {
                    Log.e("StudyApplyViewModel", "Exception: ${it.message}")
                } ?: run {
                    response?.let {
                        val errorBody = it.errorBody()?.string() ?: "없음"
                        Log.e("StudyApplyViewModel", "HTTP Error: ${it.code()} $errorBody")
                    }
                }
            }
        )
    }


    suspend fun getCommitComments(commitId: Int, studyInfoId: Int) {
        safeApiCall(
            apiCall = { gitudyCommitsRepository.getCommitComments(commitId, studyInfoId) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    _commentState.value = response.body() ?: emptyList()
                } else {
                    Log.e(
                        "CommitDetailViewModel",
                        "commitCommentListResponse status: ${response.code()}\ncommitCommentListResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }


    suspend fun makeCommitComment(commitId: Int, studyInfoId: Int, content: String) {
        val request = CommitCommentRequest(content = content, studyInfoId = studyInfoId)
        safeApiCall(
            apiCall = { gitudyCommitsRepository.makeCommitComment(commitId, request) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    viewModelScope.launch {
                        getCommitComments(commitId, studyInfoId)
                    }
                    Log.d("CommitDetailViewModel", response.code().toString())
                } else {
                    Log.e(
                        "CommitDetailViewModel",
                        "newCommitCommentResponse status: ${response.code()}\nnewCommitCommentResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }

    suspend fun deleteCommitComment(commitId: Int, commentId: Int, studyInfoId: Int) {
        safeApiCall(
            apiCall = {
                gitudyCommitsRepository.deleteCommitComment(
                    commitId = commitId,
                    commentId = commentId
                )
            },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    viewModelScope.launch {
                        getCommitComments(commitId, studyInfoId)
                    }
                    Log.d("CommitDetailViewModel", "deleteCommitCommentResponse: ${response.code()}")
                } else {
                    Log.e(
                        "CommitDetailViewModel",
                        "deleteCommitCommentResponse status: ${response.code()}\ndeleteCommitCommentResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }
}