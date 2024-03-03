package com.takseha.data.api

import com.takseha.data.dto.LoginPageResponse
import com.takseha.data.dto.LoginResponse
import com.takseha.data.dto.RegisterRequest
import com.takseha.data.dto.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GitudyApi {
    @GET("/auth/loginPage")
    suspend fun getLoginPage(): LoginPageResponse

    @GET("/auth/{platformType}/login")
    suspend fun getLoginTokens(
        @Path("platformType") platformType: String,
        @Query("code") code: String,
        @Query("state") state: String
    ): LoginResponse

    @POST("/auth/register")
    suspend fun getRegisterTokens(
        @Body request: RegisterRequest
    ): RegisterResponse
}