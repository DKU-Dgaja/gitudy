package com.takseha.data.api.gitudy.member

import com.takseha.data.dto.feed.MessageRequest
import com.takseha.data.dto.mystudy.StudyMemberListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GitudyMemberService {
    @POST("/member/{studyInfoId}/apply")
    suspend fun applyStudy(
        @Path("studyInfoId") studyInfoId: Int,
        @Query("joinCode") joinCode: String,
        @Body request: MessageRequest
    ): Response<Void>

    @POST("/member/{studyInfoId}/notify/leader")
    suspend fun notifyToLeader(
        @Path("studyInfoId") studyInfoId: Int,
        @Body request: MessageRequest
    ): Response<Void>

    @GET("/member/{studyInfoId}")
    suspend fun getStudyMemberList(
        @Path("studyInfoId") studyInfoId: Int,
        @Query("orderByScore") orderByScore: Boolean?,
    ): Response<StudyMemberListResponse>
}