package com.takseha.data.repository

import com.takseha.data.api.gitudy.GitudyApi
import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.dto.auth.register.RegisterRequest

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
}
