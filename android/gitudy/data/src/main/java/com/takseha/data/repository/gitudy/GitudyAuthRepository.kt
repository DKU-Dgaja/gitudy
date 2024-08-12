package com.takseha.data.repository.gitudy

import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.api.gitudy.GitudyAuthService

class GitudyAuthRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyAuthService::class.java)

    suspend fun checkCorrectNickname(
        request: String
    ) = client.checkCorrectNickname(request)

    suspend fun logout(
        token: String
    ) = client.logout(token)

    suspend fun getUserInfo(
    ) = client.getUserInfo()
}
