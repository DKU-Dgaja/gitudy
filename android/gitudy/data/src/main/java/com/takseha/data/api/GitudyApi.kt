package com.takseha.data.api

import com.takseha.data.BuildConfig
import com.takseha.data.dto.LoginPageResponse
import com.takseha.data.dto.LoginResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GitudyApi {
    @GET("/auth/loginPage")
    suspend fun getLoginPage(): LoginPageResponse

    @GET("/auth/{platformType}/login")
    suspend fun getAllTokens(
        @Path("platformType") platformType: String,
        @Query("code") code: String,
        @Query("state") state: String
    ): LoginResponse

}