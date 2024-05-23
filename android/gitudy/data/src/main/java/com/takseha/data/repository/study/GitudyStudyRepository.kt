package com.takseha.data.repository.study

import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.api.gitudy.study.GitudyStudyApi
import com.takseha.data.dto.feed.MakeStudyRequest

class GitudyStudyRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyStudyApi::class.java)

    suspend fun getStudyList(
        bearerToken: String,
        cursorIdx: Long?,
        limit: Long,
        sortBy: String,
        myStudy: Boolean
    ) = client.getStudyList(bearerToken, cursorIdx, limit, sortBy, myStudy)

    suspend fun makeNewStudy(
        bearerToken: String,
        request: MakeStudyRequest
    ) = client.makeNewStudy(bearerToken, request)

    suspend fun getTodoList(
        bearerToken: String,
        studyInfoId: Int,
        cursorIdx: Long?,
        limit: Long,
    ) = client.getTodoList(bearerToken, studyInfoId, cursorIdx, limit)

    suspend fun getTodoProgress(
        bearerToken: String,
        studyInfoId: Int
    ) = client.getTodoProgress(bearerToken, studyInfoId)
}
