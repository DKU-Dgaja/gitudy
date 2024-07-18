package com.takseha.data.api.gitudy.study

import com.takseha.data.dto.feed.MakeStudyRequest
import com.takseha.data.dto.feed.StudyListResponse
import com.takseha.data.dto.mystudy.ConventionResponse
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.dto.mystudy.MyStudyInfoResponse
import com.takseha.data.dto.mystudy.SetConventionRequest
import com.takseha.data.dto.mystudy.SetConventionResponse
import com.takseha.data.dto.mystudy.TodoListResponse
import com.takseha.data.dto.mystudy.TodoProgressResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GitudyStudyService {
    @GET("/study/")
    suspend fun getStudyList(
        @Query("cursorIdx") cursorIdx: Long?,
        @Query("limit") limit: Long,
        @Query("sortBy") sortBy: String,
        @Query("myStudy") myStudy: Boolean
    ): Response<StudyListResponse>

    @POST("/study/")
    suspend fun makeNewStudy(
        @Body request: MakeStudyRequest
    ): Response<Void>

    @GET("/study/{studyInfoId}/todo")
    suspend fun getTodoList(
        @Path("studyInfoId") studyInfoId: Int,
        @Query("cursorIdx") cursorIdx: Long?,
        @Query("limit") limit: Long,
    ): Response<TodoListResponse>

    @POST("/study/{studyInfoId}/todo")
    suspend fun makeNewTodo(
        @Path("studyInfoId") studyInfoId: Int,
        @Body request: MakeTodoRequest
    ): Response<Void>

    @GET("/study/{studyInfoId}/todo/progress")
    suspend fun getTodoProgress(
        @Path("studyInfoId") studyInfoId: Int
    ): Response<TodoProgressResponse>

    @GET("/study/{studyInfoId}")
    suspend fun getMyStudyInfo(
        @Path("studyInfoId") studyInfoId: Int
    ): Response<MyStudyInfoResponse>

    @POST("/study/{studyInfoId}/convention")
    suspend fun setConvention(
        @Path("studyInfoId") studyInfoId: Int,
        @Body request: SetConventionRequest
    ): Response<SetConventionResponse>

    @GET("/study/{studyInfoId}/convention")
    suspend fun getConvention(
        @Path("studyInfoId") studyInfoId: Int,
        @Query("cursorIdx") cursorIdx: Int?,
        @Query("limit") limit: Int
    ): Response<ConventionResponse>
}