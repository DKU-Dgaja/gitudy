package com.takseha.data.repository.member

import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.api.gitudy.member.GitudyMemberApi
import com.takseha.data.dto.feed.MessageRequest

class GitudyMemberRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyMemberApi::class.java)

    suspend fun applyStudy(
        bearerToken: String,
        studyInfoId: Int,
        joinCode: String,
        request: MessageRequest
    ) = client.applyStudy(bearerToken, studyInfoId, joinCode, request)

    suspend fun notifyToLeader(
        bearerToken: String,
        studyInfoId: Int,
        request: MessageRequest
    ) = client.notifyToLeader(bearerToken, studyInfoId, request)

    suspend fun getStudyMemberList(
        bearerToken: String,
        studyInfoId: Int,
        orderByScore: Boolean?
    ) = client.getStudyMemberList(bearerToken, studyInfoId, orderByScore)
}
