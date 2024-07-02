package com.takseha.data.repository.member

import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.api.gitudy.member.GitudyMemberApi
import com.takseha.data.dto.feed.MessageRequest

class GitudyMemberRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyMemberApi::class.java)

    suspend fun applyStudy(
        studyInfoId: Int,
        joinCode: String,
        request: MessageRequest
    ) = client.applyStudy(studyInfoId, joinCode, request)

    suspend fun notifyToLeader(
        studyInfoId: Int,
        request: MessageRequest
    ) = client.notifyToLeader(studyInfoId, request)

    suspend fun getStudyMemberList(
        studyInfoId: Int,
        orderByScore: Boolean?
    ) = client.getStudyMemberList(studyInfoId, orderByScore)
}
