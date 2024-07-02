package com.takseha.data.repository.auth

import com.takseha.data.api.gitudy.auth.GitudyAuthApi
import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.dto.auth.register.RegisterRequest

class GitudyAuthRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyAuthApi::class.java)
    suspend fun getLoginPage() = client.getLoginPage()

    suspend fun checkCorrectNickname(
        request: String
    ) = client.checkCorrectNickname(request)

    suspend fun register(
        request: RegisterRequest
    ) = client.register(request)

    suspend fun getUserInfo(
    ) = client.getUserInfo()
}
