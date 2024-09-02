package com.takseha.data.repository.gitudy

import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.api.gitudy.GitudyAuthService
import com.takseha.data.dto.auth.auth.UserInfoUpdateRequest
import com.takseha.data.dto.auth.register.CheckNicknameRequest

class GitudyAuthRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyAuthService::class.java)

    suspend fun checkCorrectNickname(
        request: CheckNicknameRequest
    ) = client.checkCorrectNickname(request)

    suspend fun getUserInfo(
    ) = client.getUserInfo()

    suspend fun getUserInfoUpdatePage(
    ) = client.getUserInfoUpdatePage()

    suspend fun updateUserInfo(
        request: UserInfoUpdateRequest
    ) = client.updateUserInfo(request)

    suspend fun updatePushAlarmYn(
        pushAlarmEnable: Boolean
    ) = client.updatePushAlarmYn(pushAlarmEnable)
}
