package com.takseha.data.api.gitudy.member

import com.takseha.data.dto.feed.MessageRequest
import com.takseha.data.dto.mystudy.RogerResponse
import com.takseha.data.dto.mystudy.StudyMemberListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GitudyMemberApi {
    @POST("/member/{studyInfoId}/apply")
    suspend fun applyStudy(
        @Header("Authorization") bearerToken: String,
        @Path("studyInfoId") studyInfoId: Int,
        @Query("joinCode") joinCode: String,
        @Body request: MessageRequest
    ): Response<RogerResponse>

    @POST("/member/{studyInfoId}/notify/leader")
    suspend fun notifyToLeader(
        @Header("Authorization") bearerToken: String,
        @Path("studyInfoId") studyInfoId: Int,
        @Body request: MessageRequest
    ): Response<RogerResponse>

    @GET("/member/{studyInfoId}")
    suspend fun getStudyMemberList(
        @Header("Authorization") bearerToken: String,
        @Path("studyInfoId") studyInfoId: Int,
        @Query("orderByScore") orderByScore: Boolean?,
    ): Response<StudyMemberListResponse>
}