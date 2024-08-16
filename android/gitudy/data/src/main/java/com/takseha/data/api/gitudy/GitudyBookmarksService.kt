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

interface GitudyBookmarksService {
    @GET("/bookmarks")
    suspend fun getBookmarks(
        @Query("cursorIdx") cursorIdx: Long?,
        @Query("limit") limit: Long
    ): Response<StudyApplyMemberListResponse>

    @GET("/bookmarks/study/{studyInfoId}")
    suspend fun setBookmarkStatus(
        @Path("studyInfoId") studyInfoId: Int
    ): Response<Void>
}