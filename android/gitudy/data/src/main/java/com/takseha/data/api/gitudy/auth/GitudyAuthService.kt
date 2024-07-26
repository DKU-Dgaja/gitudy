package com.takseha.data.api.gitudy.auth

import com.takseha.data.dto.auth.auth.UserInfoResponse
import com.takseha.data.dto.auth.login.LoginPageInfoResponse
import com.takseha.data.dto.auth.login.TokenResponse
import com.takseha.data.dto.auth.login.ReissueTokenResponse
import com.takseha.data.dto.auth.register.RegisterRequest
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

    @POST("/auth/check-nickname")
    suspend fun checkCorrectNickname(
        @Body request: String
    ): Response<Void>

    @POST("/auth/register")
    suspend fun getRegisterTokens(
        @Header("Authorization") token: String,
        @Body request: RegisterRequest
    ): Response<TokenResponse>

    @POST("/auth/reissue")
    suspend fun reissueTokens(
        @Header("Authorization") token: String
    ): Response<ReissueTokenResponse>

    @GET("/auth/info")
    suspend fun getUserInfo(
    ): Response<UserInfoResponse>
}