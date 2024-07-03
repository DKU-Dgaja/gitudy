package com.takseha.data.repository.study

import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.api.gitudy.study.GitudyStudyApi
import com.takseha.data.dto.feed.MakeStudyRequest
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.dto.mystudy.SetConventionRequest

class GitudyStudyRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyStudyApi::class.java)

    suspend fun getStudyList(
        cursorIdx: Long?,
        limit: Long,
        sortBy: String,
        myStudy: Boolean
    ) = client.getStudyList(cursorIdx, limit, sortBy, myStudy)

    suspend fun makeNewStudy(
        request: MakeStudyRequest
    ) = client.makeNewStudy(request)

    suspend fun getTodoList(
        studyInfoId: Int,
        cursorIdx: Long?,
        limit: Long,
    ) = client.getTodoList(studyInfoId, cursorIdx, limit)

    suspend fun makeNewTodo(
        studyInfoId: Int,
        request: MakeTodoRequest
    ) = client.makeNewTodo(studyInfoId, request)

    suspend fun getTodoProgress(
        studyInfoId: Int
    ) = client.getTodoProgress(studyInfoId)

    suspend fun getMyStudyInfo(
        studyInfoId: Int
    ) = client.getMyStudyInfo(studyInfoId)

    suspend fun setConvention(
        studyInfoId: Int,
        request: SetConventionRequest
    ) = client.setConvention(studyInfoId, request)

    suspend fun getConvention(
        studyInfoId: Int,
    ) = client.getConvention(studyInfoId, null, 1)
}
