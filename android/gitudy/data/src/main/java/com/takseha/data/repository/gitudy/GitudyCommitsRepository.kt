package com.takseha.data.repository.gitudy

import com.takseha.data.api.gitudy.GitudyCommitsService
import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.api.gitudy.GitudyStudyService
import com.takseha.data.dto.feed.CheckRepoNameRequest
import com.takseha.data.dto.feed.MakeStudyRequest
import com.takseha.data.dto.mystudy.CommitCommentRequest
import com.takseha.data.dto.mystudy.CommitRejectRequest
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.dto.mystudy.SetConventionRequest
import com.takseha.data.dto.mystudy.UpdateStudyInfoRequest
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.Query

class GitudyCommitsRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyCommitsService::class.java)

    suspend fun getMyCommitList(
        studyInfoId: Int?,
        cursorIdx: Long?,
        limit: Long
    ) = client.getMyCommitList(studyInfoId, cursorIdx, limit)

    suspend fun approveCommit(
        commitId: Int,
        studyInfoId: Int
    ) = client.approveCommit(commitId, studyInfoId)

    suspend fun rejectCommit(
        commitId: Int,
        studyInfoId: Int,
        request: CommitRejectRequest,
    ) = client.rejectCommit(commitId, studyInfoId, request)

    suspend fun getCommitComments(
        commitId: Int,
        studyInfoId: Int
    ) = client.getCommitComments(commitId, studyInfoId)

    suspend fun makeCommitComment(
        commitId: Int,
        request: CommitCommentRequest
    ) = client.makeCommitComment(commitId, request)

    suspend fun deleteCommitComment(
        commitId: Int,
        commentId: Int
    ) = client.deleteCommitComment(commitId, commentId)
}
