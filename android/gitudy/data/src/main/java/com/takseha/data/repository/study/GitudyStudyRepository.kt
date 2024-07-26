package com.takseha.data.repository.study

import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.api.gitudy.study.GitudyStudyService
import com.takseha.data.dto.feed.MakeStudyRequest
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.dto.mystudy.SetConventionRequest

class GitudyStudyRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyStudyService::class.java)

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

    suspend fun getTodo(
        studyInfoId: Int,
        todoId: Int
    ) = client.getTodo(studyInfoId, todoId)

    suspend fun updateTodo(
        studyInfoId: Int,
        todoId: Int,
        request: MakeTodoRequest
    ) = client.updateTodo(studyInfoId, todoId, request)

    suspend fun deleteTodo(
        studyInfoId: Int,
        todoId: Int
    ) = client.deleteTodo(studyInfoId, todoId)

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
