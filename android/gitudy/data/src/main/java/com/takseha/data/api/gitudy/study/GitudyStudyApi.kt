package com.takseha.data.api.gitudy.study

import com.takseha.data.dto.feed.MakeStudyRequest
import com.takseha.data.dto.feed.MakeStudyResponse
import com.takseha.data.dto.feed.StudyListResponse
import com.takseha.data.dto.mystudy.TodoListResponse
import com.takseha.data.dto.mystudy.TodoProgressResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GitudyStudyApi {
    @GET("/study/")
    suspend fun getStudyList(
        @Header("Authorization") bearerToken: String,
        @Query("cursorIdx") cursorIdx: Long?,
        @Query("limit") limit: Long,
        @Query("sortBy") sortBy: String,
        @Query("myStudy") myStudy: Boolean
    ): Response<StudyListResponse>

    @POST("/study/")
    suspend fun makeNewStudy(
        @Header("Authorization") bearerToken: String,
        @Body request: MakeStudyRequest
    ): Response<MakeStudyResponse>

    @GET("/study/{studyInfoId}/todo")
    suspend fun getTodoList(
        @Header("Authorization") bearerToken: String,
        @Path("studyInfoId") studyInfoId: Int,
        @Query("cursorIdx") cursorIdx: Long?,
        @Query("limit") limit: Long,
    ): Response<TodoListResponse>

    @GET("/study/{studyInfoId}/todo/progress")
    suspend fun getTodoProgress(
        @Header("Authorization") bearerToken: String,
        @Path("studyInfoId") studyInfoId: Int
    ): Response<TodoProgressResponse>
}