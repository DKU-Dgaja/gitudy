package com.takseha.data.repository.gitudy

import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.api.gitudy.GitudyMemberService
import com.takseha.data.dto.feed.MessageRequest

class GitudyMemberRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyMemberService::class.java)

    suspend fun getStudyApplyMemberList(
        studyInfoId: Int,
        cursorIdx: Long?,
        limit: Long
    ) = client.getStudyApplyMemberList(studyInfoId, cursorIdx, limit)

    suspend fun applyStudy(
        studyInfoId: Int,
        joinCode: String,
        request: MessageRequest
    ) = client.applyStudy(studyInfoId, joinCode, request)

    suspend fun withdrawApplyStudy(
        studyInfoId: Int
    ) = client.withdrawApplyStudy(studyInfoId)

    suspend fun notifyToLeader(
        studyInfoId: Int,
        request: MessageRequest
    ) = client.notifyToLeader(studyInfoId, request)

    suspend fun getStudyMemberList(
        studyInfoId: Int,
        orderByScore: Boolean?
    ) = client.getStudyMemberList(studyInfoId, orderByScore)

    suspend fun approveApplyMember(
        studyInfoId: Int,
        applyUserId: Int,
        approve: Boolean
    ) = client.approveApplyMember(studyInfoId, applyUserId, approve)

    suspend fun withdrawStudy(
        studyInfoId: Int
    ) = client.withdrawStudy(studyInfoId)
}
