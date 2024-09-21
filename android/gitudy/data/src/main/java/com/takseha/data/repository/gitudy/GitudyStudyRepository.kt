package com.takseha.data.repository.gitudy

import com.takseha.data.api.gitudy.GitudyStudyService
import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.dto.feed.CheckRepoNameRequest
import com.takseha.data.dto.feed.MakeStudyRequest
import com.takseha.data.dto.mystudy.CommentRequest
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.dto.mystudy.SetConventionRequest
import com.takseha.data.dto.mystudy.UpdateStudyInfoRequest

class GitudyStudyRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyStudyService::class.java)

    suspend fun getStudyList(
        cursorIdx: Long?,
        limit: Long,
        sortBy: String,
        myStudy: Boolean
    ) = client.getStudyList(cursorIdx, limit, sortBy, myStudy)

    suspend fun getStudyCount(
        myStudy: Boolean
    ) = client.getStudyCount(myStudy)

    suspend fun getStudyRank(
        studyInfoId: Int
    ) = client.getStudyRank(studyInfoId)

    suspend fun makeNewStudy(
        request: MakeStudyRequest
    ) = client.makeNewStudy(request)

    suspend fun checkValidRepoName(
        request: CheckRepoNameRequest
    ) = client.checkValidRepoName(request)

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

    suspend fun getStudyInfo(
        studyInfoId: Int
    ) = client.getStudyInfo(studyInfoId)

    suspend fun deleteStudy(
        studyInfoId: Int
    ) = client.deleteStudy(studyInfoId)

    suspend fun endStudy(
        studyInfoId: Int
    ) = client.endStudy(studyInfoId)

    suspend fun setConvention(
        studyInfoId: Int,
        request: SetConventionRequest
    ) = client.setConvention(studyInfoId, request)

    suspend fun getConvention(
        studyInfoId: Int,
    ) = client.getConvention(studyInfoId, null, 1)

    suspend fun getStudyComments(
        studyInfoId: Int,
        cursorIdx: Long?,
        limit: Long,
    ) = client.getStudyComments(studyInfoId, cursorIdx, limit)

    suspend fun makeStudyComment(
        studyInfoId: Int,
        request: CommentRequest
    ) = client.makeStudyComment(studyInfoId, request)

    suspend fun updateStudyComment(
        studyInfoId: Int,
        studyCommentId: Int,
        request: CommentRequest
    ) = client.updateStudyComment(studyInfoId, studyCommentId, request)

    suspend fun updateStudyInfo(
        studyInfoId: Int,
        request: UpdateStudyInfoRequest
    ) = client.updateStudyInfo(studyInfoId, request)

    suspend fun deleteStudyComment(
        studyInfoId: Int,
        studyCommentId: Int
    ) = client.deleteStudyComment(studyInfoId, studyCommentId)
}
