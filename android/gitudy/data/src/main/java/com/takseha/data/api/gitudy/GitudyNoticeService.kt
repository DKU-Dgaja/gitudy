package com.takseha.data.api.gitudy

import com.takseha.data.dto.home.NoticeResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitudyNoticeService {
    @GET("/notice")
    suspend fun getNoticeList(
        @Query("cursorTime") cursorTime: String?,
        @Query("limit") limit: Long
    ): Response<NoticeResponse>

    @DELETE("/notice")
    suspend fun deleteAllNotice(
    ): Response<Void>

    @DELETE("/notice/{id}")
    suspend fun deleteNotice(
        @Path("id") id: String
    ): Response<Void>
}