package com.takseha.data.api

import com.takseha.data.BuildConfig
import retrofit2.http.GET

interface GitudyApi {
    @GET(BuildConfig.LOGIN_PAGE_API)
    suspend fun getLoginPages()
}