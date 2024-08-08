package com.takseha.data.api.gitudy

import com.takseha.data.dto.feed.CategoryListResponse
import com.takseha.data.dto.mystudy.StudyCategoryResponse
import retrofit2.Response
import retrofit2.http.GET
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