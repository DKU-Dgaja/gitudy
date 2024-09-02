package com.takseha.data.api.gitudy

import com.takseha.data.dto.feed.MessageRequest
import com.takseha.data.dto.mystudy.StudyApplyMemberListResponse
import com.takseha.data.dto.mystudy.StudyMemberListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GitudyMemberService {
    @GET("/member/{studyInfoId}/apply")
    suspend fun getStudyApplyMemberList(
        @Path("studyInfoId") studyInfoId: Int,
        @Query("cursorIdx") cursorIdx: Long?,
        @Query("limit") limit: Long,
    ): Response<StudyApplyMemberListResponse>

    @POST("/member/{studyInfoId}/apply")
    suspend fun applyStudy(
        @Path("studyInfoId") studyInfoId: Int,
        @Query("joinCode") joinCode: String,
        @Body request: MessageRequest
    ): Response<Void>

    @DELETE("/member/{studyInfoId}/apply")
    suspend fun withdrawApplyStudy(
        @Path("studyInfoId") studyInfoId: Int
    ): Response<Void>

    @POST("/member/{studyInfoId}/notify/leader")
    suspend fun notifyToLeader(
        @Path("studyInfoId") studyInfoId: Int,
        @Body request: MessageRequest
    ): Response<Void>

    @GET("/member/{studyInfoId}")
    suspend fun getStudyMemberList(
        @Path("studyInfoId") studyInfoId: Int,
        @Query("orderByScore") orderByScore: Boolean?
    ): Response<StudyMemberListResponse>

    @PATCH("/member/{studyInfoId}/apply/{applyUserId}")
    suspend fun approveApplyMember(
        @Path("studyInfoId") studyInfoId: Int,
        @Path("applyUserId") applyUserId: Int,
        @Query("approve") approve: Boolean
    ): Response<Void>

    @PATCH("/member/{studyInfoId}/withdrawal")
    suspend fun withdrawStudy(
        @Path("studyInfoId") studyInfoId: Int
    ): Response<Void>
}