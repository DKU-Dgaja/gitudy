package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.CommitRejectRequest
import com.takseha.data.dto.mystudy.MakeTodoRequest
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

    fun getRepositoryInfo(studyInfoId: Int) = viewModelScope.launch {
        safeApiCall(
            apiCall = { gitudyStudyRepository.getStudyInfo(studyInfoId) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val repoInfo = response.body()!!.githubLinkInfo
                    _repositoryInfoState.update { it.copy(
                        owner = repoInfo.owner,
                        name = repoInfo.name,
                        branchName = repoInfo.branchName
                    ) }
                } else {
                    Log.e(
                        "CommitDetailViewModel",
                        "myStudyInfoResponse status: ${response.code()}\nmyStudyInfoResponse message: ${response.errorBody()?.string()}"
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
                    Log.e("CommitDetailViewModel", "approveCommitResponse status: ${response.code()}\napproveCommitResponse message: ${response.errorBody()?.string()}")
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
                    Log.e("CommitDetailViewModel", "rejectCommitResponse status: ${response.code()}\nrejectCommitResponse message: ${response.errorBody()?.string()}")
                }
            }
        )
    }
}