package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import com.takseha.data.dto.mystudy.RepositoryInfo
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CommitDetailViewModel : ViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()

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
}