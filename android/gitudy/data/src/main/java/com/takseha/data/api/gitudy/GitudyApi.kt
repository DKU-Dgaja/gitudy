package com.takseha.data.api.gitudy

import com.takseha.data.dto.auth.login.LoginPageResponse
import com.takseha.data.dto.auth.login.LoginResponse
import com.takseha.data.dto.auth.register.RegisterRequest
import com.takseha.data.dto.auth.register.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GitudyApi {
    @GET("/auth/loginPage")
    suspend fun getLoginPage(): Response<LoginPageResponse>

    @GET("/auth/{platformType}/login")
    suspend fun getLoginTokens(
        @Path("platformType") platformType: String,
        @Query("code") code: String,
        @Query("state") state: String
    ): Response<LoginResponse>

    @POST("/auth/register")
    suspend fun getRegisterTokens(
        @Header("Authorization") bearerToken: String,
        @Body request: RegisterRequest
    ): Response<RegisterResponse>
}