package com.takseha.data.repository.gitudy

import com.takseha.data.api.gitudy.GitudyNoticeService
import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.api.gitudy.GitudyStudyService
import com.takseha.data.dto.feed.CheckRepoNameRequest
import com.takseha.data.dto.feed.MakeStudyRequest
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.dto.mystudy.SetConventionRequest

class GitudyNoticeRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyNoticeService::class.java)

    suspend fun getNoticeList(
        cursorTime: String?,
        limit: Long
    ) = client.getNoticeList(cursorTime, limit)

    suspend fun deleteAllNotice(
    ) = client.deleteAllNotice()

    suspend fun deleteNotice(
        id: String
    ) = client.deleteNotice(id)
}
