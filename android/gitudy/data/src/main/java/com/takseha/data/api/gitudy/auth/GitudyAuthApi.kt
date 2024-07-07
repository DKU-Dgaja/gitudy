package com.takseha.data.api.gitudy.auth

import com.takseha.data.dto.auth.auth.UserInfoResponse
import com.takseha.data.dto.auth.login.LoginPageResponse
import com.takseha.data.dto.auth.login.LoginResponse
import com.takseha.data.dto.auth.register.NickNameResponse
import com.takseha.data.dto.auth.register.RegisterRequest
import com.takseha.data.dto.auth.register.RegisterResponse
import com.takseha.data.dto.auth.register.ReissueResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GitudyAuthApi {
    @GET("/auth/loginPage")
    suspend fun getLoginPage(): Response<LoginPageResponse>

    @GET("/auth/{platformType}/login")
    suspend fun getLoginTokens(
        @Path("platformType") platformType: String,
        @Query("code") code: String,
        @Query("state") state: String
    ): Response<LoginResponse>

    @POST("/auth/check-nickname")
    suspend fun checkCorrectNickname(
        @Body request: String
    ): Response<NickNameResponse>

    @POST("/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("/auth/reissue")
    suspend fun reissueTokens(bearerToken: String): Response<ReissueResponse>

    @GET("/auth/info")
    suspend fun getUserInfo(
    ): Response<UserInfoResponse>
}