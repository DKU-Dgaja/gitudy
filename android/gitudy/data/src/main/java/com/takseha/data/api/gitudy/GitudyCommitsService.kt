package com.takseha.data.api.gitudy

import com.takseha.data.dto.feed.CheckRepoNameRequest
import com.takseha.data.dto.feed.MakeStudyRequest
import com.takseha.data.dto.feed.StudyCountResponse
import com.takseha.data.dto.feed.StudyListResponse
import com.takseha.data.dto.feed.StudyRankResponse
import com.takseha.data.dto.mystudy.CommitRejectRequest
import com.takseha.data.dto.mystudy.ConventionResponse
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.dto.mystudy.StudyInfoResponse
import com.takseha.data.dto.mystudy.SetConventionRequest
import com.takseha.data.dto.mystudy.StudyCommentListResponse
import com.takseha.data.dto.mystudy.Todo
import com.takseha.data.dto.mystudy.TodoListResponse
import com.takseha.data.dto.mystudy.TodoProgressResponse
import com.takseha.data.dto.mystudy.UpdateStudyInfoRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface GitudyCommitsService {
    @GET("/commits/{commitId}/approve")
    suspend fun approveCommit(
        @Query("studyInfoId") studyInfoId: Int,
        @Path("commitId") commitId: Int
    ): Response<Void>

    @GET("/commits/{commitId}/reject")
    suspend fun rejectCommit(
        @Query("studyInfoId") studyInfoId: Int,
        @Query("request") request: CommitRejectRequest,
        @Path("commitId") commitId: Int
    ): Response<Void>
}