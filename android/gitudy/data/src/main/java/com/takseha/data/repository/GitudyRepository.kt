package com.takseha.data.repository

import com.takseha.data.api.gitudy.GitudyApi
import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.dto.auth.register.RegisterRequest
import retrofit2.http.Query

class GitudyRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyApi::class.java)
    suspend fun getLoginPage() = client.getLoginPage()
    suspend fun getLoginTokens(
        platformType: String,
        code: String,
        state: String
    ) = client.getLoginTokens(platformType, code, state)
    suspend fun getRegisterTokens(
        bearerToken: String,
        request: RegisterRequest
    ) = client.getRegisterTokens(bearerToken, request)

    suspend fun getUserInfo(
        bearerToken: String
    ) = client.getUserInfo(bearerToken)

    suspend fun getMyStudyList(
        bearerToken: String,
        userId: Int,
        cursorIdx: Int?,
        sortBy: String = "createdDateTime"
    ) = client.getMyStudyList(bearerToken, userId, cursorIdx, limit = 20, sortBy, myStudy = true)
}
