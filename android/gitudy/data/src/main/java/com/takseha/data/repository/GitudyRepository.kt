package com.takseha.data.repository

import com.takseha.data.api.GitudyApi
import com.takseha.data.api.RetrofitInstance
import com.takseha.data.dto.RegisterRequest

class GitudyRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyApi::class.java)
    suspend fun getLoginPage() = client.getLoginPage()
    suspend fun getLoginTokens(
        platformType: String,
        code: String,
        state: String
    ) = client.getLoginTokens(platformType, code, state)
    suspend fun getRegisterTokens(
        request: RegisterRequest
    ) = client.getRegisterTokens(request)
}
