package com.takseha.data.repository.auth

import com.takseha.data.api.gitudy.auth.GitudyAuthApi
import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.dto.auth.register.RegisterRequest
import com.takseha.data.dto.feed.MakeStudyRequest

class GitudyAuthRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyAuthApi::class.java)
    suspend fun getLoginPage() = client.getLoginPage()
    suspend fun getLoginTokens(
        platformType: String,
        code: String,
        state: String
    ) = client.getLoginTokens(platformType, code, state)

    suspend fun checkCorrectNickname(
        request: String
    ) = client.checkCorrectNickname(request)

    suspend fun getRegisterTokens(
        bearerToken: String,
        request: RegisterRequest
    ) = client.getRegisterTokens(bearerToken, request)

    suspend fun getUserInfo(
        bearerToken: String
    ) = client.getUserInfo(bearerToken)
}
