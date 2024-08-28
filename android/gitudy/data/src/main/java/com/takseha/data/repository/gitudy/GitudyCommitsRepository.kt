package com.takseha.data.repository.gitudy

import com.takseha.data.api.gitudy.GitudyCommitsService
import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.api.gitudy.GitudyStudyService
import com.takseha.data.dto.feed.CheckRepoNameRequest
import com.takseha.data.dto.feed.MakeStudyRequest
import com.takseha.data.dto.mystudy.CommitRejectRequest
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.dto.mystudy.SetConventionRequest
import com.takseha.data.dto.mystudy.UpdateStudyInfoRequest

class GitudyCommitsRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyCommitsService::class.java)

    suspend fun approveCommit(
        studyInfoId: Int,
        commitId: Int
    ) = client.approveCommit(studyInfoId, commitId)

    suspend fun rejectCommit(
        studyInfoId: Int,
        request: CommitRejectRequest,
        commitId: Int
    ) = client.rejectCommit(studyInfoId, request, commitId)
}
