package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import com.takseha.data.dto.mystudy.CommitRejectRequest
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.dto.mystudy.RepositoryInfo
import com.takseha.data.repository.gitudy.GitudyCommitsRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CommitDetailViewModel : ViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()
    private var gitudyCommitsRepository = GitudyCommitsRepository()

    private val _repositoryInfoState = MutableStateFlow(RepositoryInfo())
    val repositoryInfoState = _repositoryInfoState.asStateFlow()

    suspend fun getRepositoryInfo(studyInfoId: Int) {
        val myStudyInfoResponse = gitudyStudyRepository.getStudyInfo(studyInfoId)

        if (myStudyInfoResponse.isSuccessful) {
            val repoInfo = myStudyInfoResponse.body()!!.githubLinkInfo
            _repositoryInfoState.update { it.copy(
                owner = repoInfo.owner,
                name = repoInfo.name,
                branchName = repoInfo.branchName
            ) }
        } else {
            Log.e(
                "CommitDetailViewModel",
                "myStudyInfoResponse status: ${myStudyInfoResponse.code()}\nmyStudyInfoResponse message: ${myStudyInfoResponse.errorBody()?.string()}"
            )
        }
    }

    suspend fun approveCommit(studyInfoId: Int, commitId: Int) {
        val approveCommitResponse = gitudyCommitsRepository.approveCommit(commitId, studyInfoId)

        if (approveCommitResponse.isSuccessful) {
            Log.d("CommitDetailViewModel", approveCommitResponse.code().toString())
        } else {
            Log.e("CommitDetailViewModel", "approveCommitResponse status: ${approveCommitResponse.code()}\napproveCommitResponse message: ${approveCommitResponse.errorBody()?.string()}")
        }
    }

    suspend fun rejectCommit(studyInfoId: Int, rejectionReason: String, commitId: Int) {
        val request = CommitRejectRequest(rejectionReason = rejectionReason)

        val rejectCommitResponse = gitudyCommitsRepository.rejectCommit(commitId, studyInfoId, request)

        if (rejectCommitResponse.isSuccessful) {
            Log.d("CommitDetailViewModel", rejectCommitResponse.code().toString())
        } else {
            Log.e("CommitDetailViewModel", "rejectCommitResponse status: ${rejectCommitResponse.code()}\nrejectCommitResponse message: ${rejectCommitResponse.errorBody()?.string()}")
        }
    }
}