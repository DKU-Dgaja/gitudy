package com.takseha.data.api.gitudy

import com.takseha.data.dto.mystudy.Commit
import com.takseha.data.dto.mystudy.CommitCommentListResponse
import com.takseha.data.dto.mystudy.CommitCommentRequest
import com.takseha.data.dto.mystudy.CommitRejectRequest
import com.takseha.data.dto.profile.MyCommitListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GitudyCommitsService {
    @GET("/commits")
    suspend fun getMyCommitList(
        @Query("studyInfoId") studyInfoId: Int?,
        @Query("cursorIdx") cursorIdx: Long?,
        @Query("limit") limit: Long
    ): Response<MyCommitListResponse>

    @GET("/commits/{commitId}")
    suspend fun getMyCommitInfo(
        @Path("commitId") commitId: Int,
        @Query("studyInfoId") studyInfoId: Int
    ): Response<Commit>

    @GET("/commits/{commitId}/approve")
    suspend fun approveCommit(
        @Path("commitId") commitId: Int,
        @Query("studyInfoId") studyInfoId: Int
    ): Response<Void>

    @POST("/commits/{commitId}/reject")
    suspend fun rejectCommit(
        @Path("commitId") commitId: Int,
        @Query("studyInfoId") studyInfoId: Int,
        @Body request: CommitRejectRequest
    ): Response<Void>

    @GET("/commits/{commitId}/comments")
    suspend fun getCommitComments(
        @Path("commitId") commitId: Int,
        @Query("studyInfoId") studyInfoId: Int
    ): Response<CommitCommentListResponse>

    @POST("/commits/{commitId}/comments")
    suspend fun makeCommitComment(
        @Path("commitId") commitId: Int,
        @Body request: CommitCommentRequest
    ): Response<Void>

    @DELETE("/commits/{commitId}/comments/{commentId}")
    suspend fun deleteCommitComment(
        @Path("commitId") commitId: Int,
        @Path("commentId") commentId: Int
    ): Response<Void>
}