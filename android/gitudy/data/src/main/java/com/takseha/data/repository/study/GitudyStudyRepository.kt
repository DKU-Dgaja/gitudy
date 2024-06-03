package com.takseha.data.repository.study

import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.api.gitudy.study.GitudyStudyApi
import com.takseha.data.dto.feed.MakeStudyRequest
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.dto.mystudy.SetConventionRequest

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

    suspend fun makeNewTodo(
        bearerToken: String,
        studyInfoId: Int,
        request: MakeTodoRequest
    ) = client.makeNewTodo(bearerToken, studyInfoId, request)

    suspend fun getTodoProgress(
        bearerToken: String,
        studyInfoId: Int
    ) = client.getTodoProgress(bearerToken, studyInfoId)

    suspend fun getMyStudyInfo(
        bearerToken: String,
        studyInfoId: Int
    ) = client.getMyStudyInfo(bearerToken, studyInfoId)

    suspend fun setConvention(
        bearerToken: String,
        studyInfoId: Int,
        request: SetConventionRequest
    ) = client.setConvention(bearerToken, studyInfoId, request)

    suspend fun getConvention(
        bearerToken: String,
        studyInfoId: Int,
    ) = client.getConvention(bearerToken, studyInfoId, null, 1)
}
