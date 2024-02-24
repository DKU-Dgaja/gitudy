package com.takseha.data.api

import com.takseha.data.BuildConfig
import com.takseha.data.dto.LoginResponse
import com.takseha.data.dto.TokenResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitudyApi {
    @GET(BuildConfig.LOGIN_PAGE_API)
    suspend fun getLoginPage(): LoginResponse

    @GET(BuildConfig.TOKEN_API)
    suspend fun getAllTokens(@Path("socialName") socialName: String, @Query("code") code: String, @Query("state") state: String): TokenResponse
}