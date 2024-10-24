package com.takseha.data.api.gitudy

import com.takseha.data.dto.auth.auth.UserInfoResponse
import com.takseha.data.dto.auth.auth.UserInfoUpdatePageResponse
import com.takseha.data.dto.auth.auth.UserInfoUpdateRequest
import com.takseha.data.dto.auth.login.AdminLoginRequest
import com.takseha.data.dto.auth.login.LoginPageInfoResponse
import com.takseha.data.dto.auth.login.ReissueTokenResponse
import com.takseha.data.dto.auth.login.TokenResponse
import com.takseha.data.dto.auth.register.CheckNicknameRequest
import com.takseha.data.dto.auth.register.RegisterRequest
import com.takseha.data.dto.feed.MessageRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GitudyAuthService {
    @GET("/auth/loginPage")
    suspend fun getLoginPage(): Response<LoginPageInfoResponse>

    @GET("/auth/{platformType}/login")
    suspend fun getLoginTokens(
        @Path("platformType") platformType: String,
        @Query("code") code: String,
        @Query("state") state: String
    ): Response<TokenResponse>

    @POST("/auth/admin")
    suspend fun getAdminTokens(
        @Body request: AdminLoginRequest
    ): Response<TokenResponse>

    @POST("/auth/register")
    suspend fun getRegisterTokens(
        @Header("Authorization") token: String,
        @Body request: RegisterRequest
    ): Response<TokenResponse>

    @POST("/auth/reissue")
    suspend fun reissueTokens(
        @Header("Authorization") token: String
    ): Response<ReissueTokenResponse>

    @POST("/auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<Void>

    @POST("/auth/delete")
    suspend fun deleteUserAccount(
        @Header("Authorization") token: String,
        @Body request: MessageRequest
    ): Response<Void>

    @POST("/auth/check-nickname")
    suspend fun checkCorrectNickname(
        @Body request: CheckNicknameRequest
    ): Response<Void>

    @GET("/auth/info")
    suspend fun getUserInfo(
    ): Response<UserInfoResponse>

    @GET("/auth/update")
    suspend fun getUserInfoUpdatePage(
    ): Response<UserInfoUpdatePageResponse>

    @GET("/auth/update/pushAlarmYn/{pushAlarmEnable}")
    suspend fun updatePushAlarmYn(
        @Path("pushAlarmEnable") pushAlarmEnable: Boolean
    ): Response<Void>

    @POST("/auth/update")
    suspend fun updateUserInfo(
        @Body request: UserInfoUpdateRequest
    ): Response<Void>
}