package com.takseha.data.api.gitudy.category

import com.takseha.data.dto.feed.CategoryListResponse
import com.takseha.data.dto.feed.MessageRequest
import com.takseha.data.dto.mystudy.StudyCategoryResponse
import com.takseha.data.dto.mystudy.StudyMemberListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GitudyCategoryService {
    @GET("/category/")
    suspend fun getAllCategory(
    ): Response<CategoryListResponse>

    @GET("/category/{studyInfoId}")
    suspend fun getStudyCategory(
        @Path("studyInfoId") studyInfoId: Int,
        @Query("cursorIdx") cursorIdx: Long?,
        @Query("limit") limit: Int
    ): Response<StudyCategoryResponse>
}