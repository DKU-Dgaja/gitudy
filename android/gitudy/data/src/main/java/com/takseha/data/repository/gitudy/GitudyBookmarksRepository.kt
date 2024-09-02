package com.takseha.data.repository.gitudy

import com.takseha.data.api.gitudy.GitudyBookmarksService
import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.api.gitudy.GitudyMemberService
import com.takseha.data.dto.feed.MessageRequest

class GitudyBookmarksRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyBookmarksService::class.java)

    suspend fun getBookmarks(
        cursorIdx: Long?,
        limit: Long
    ) = client.getBookmarks(cursorIdx, limit)

    suspend fun setBookmarkStatus(
        studyInfoId: Int
    ) = client.setBookmarkStatus(studyInfoId)

    suspend fun checkBookmarkStatus(
        studyInfoId: Int
    ) = client.checkBookmarkStatus(studyInfoId)
}
