package com.takseha.data.api.gitudy

import com.takseha.data.dto.feed.CheckRepoNameRequest
import com.takseha.data.dto.feed.MakeStudyRequest
import com.takseha.data.dto.feed.StudyCountResponse
import com.takseha.data.dto.feed.StudyListResponse
import com.takseha.data.dto.mystudy.ConventionResponse
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.dto.mystudy.StudyInfoResponse
import com.takseha.data.dto.mystudy.SetConventionRequest
import com.takseha.data.dto.mystudy.StudyCommentListResponse
import com.takseha.data.dto.mystudy.Todo
import com.takseha.data.dto.mystudy.TodoListResponse
import com.takseha.data.dto.mystudy.TodoProgressResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @GET("/study/count")
    suspend fun getStudyCount(
        @Query("myStudy") myStudy: Boolean
    ): Response<StudyCountResponse>

    @POST("/study/")
    suspend fun makeNewStudy(
        @Body request: MakeStudyRequest
    ): Response<Void>

    @POST("/study/check-name")
    suspend fun checkValidRepoName(
        @Body request: CheckRepoNameRequest
    ): Response<Void>

    @GET("/study/{studyInfoId}/todo")
    suspend fun getTodoList(
        @Path("studyInfoId") studyInfoId: Int,
        @Query("cursorIdx") cursorIdx: Long?,
        @Query("limit") limit: Long,
    ): Response<TodoListResponse>

    @GET("/study/{studyInfoId}/todo/{todoId}")
    suspend fun getTodo(
        @Path("studyInfoId") studyInfoId: Int,
        @Path("todoId") todoId: Int,
    ): Response<Todo>

    @PUT("/study/{studyInfoId}/todo/{todoId}")
    suspend fun updateTodo(
        @Path("studyInfoId") studyInfoId: Int,
        @Path("todoId") todoId: Int,
        @Body request: MakeTodoRequest
    ): Response<Void>

    @DELETE("/study/{studyInfoId}/todo/{todoId}")
    suspend fun deleteTodo(
        @Path("studyInfoId") studyInfoId: Int,
        @Path("todoId") todoId: Int
    ): Response<Void>

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
    suspend fun getStudyInfo(
        @Path("studyInfoId") studyInfoId: Int
    ): Response<StudyInfoResponse>

    @POST("/study/{studyInfoId}/convention")
    suspend fun setConvention(
        @Path("studyInfoId") studyInfoId: Int,
        @Body request: SetConventionRequest
    ): Response<Void>

    @GET("/study/{studyInfoId}/convention")
    suspend fun getConvention(
        @Path("studyInfoId") studyInfoId: Int,
        @Query("cursorIdx") cursorIdx: Int?,
        @Query("limit") limit: Int
    ): Response<ConventionResponse>


    @GET("/study/{studyInfoId}/comments")
    suspend fun getStudyComments(
        @Path("studyInfoId") studyInfoId: Int,
        @Query("cursorIdx") cursorIdx: Long?,
        @Query("limit") limit: Long
    ): Response<StudyCommentListResponse>

    @POST("/study/{studyInfoId}/comment")
    suspend fun makeStudyComment(
        @Path("studyInfoId") studyInfoId: Int,
        @Body content: String
    ): Response<Void>

    @PATCH("/study/{studyInfoId}/comment/{studyCommentId}")
    suspend fun updateStudyComment(
        @Path("studyInfoId") studyInfoId: Int,
        @Path("studyCommentId") studyCommentId: Int,
        @Body content: String
    ): Response<Void>

    @DELETE("/study/{studyInfoId}/comment/{studyCommentId}")
    suspend fun deleteStudyComment(
        @Path("studyInfoId") studyInfoId: Int,
        @Path("studyCommentId") studyCommentId: Int
    ): Response<Void>
}