package com.takseha.data.repository.auth

import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.api.gitudy.auth.GitudyAuthService

class GitudyAuthRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyAuthService::class.java)

    suspend fun checkCorrectNickname(
        request: String
    ) = client.checkCorrectNickname(request)

    suspend fun getUserInfo(
    ) = client.getUserInfo()
}
