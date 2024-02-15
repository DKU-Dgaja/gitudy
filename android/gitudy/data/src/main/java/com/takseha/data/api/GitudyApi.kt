package com.takseha.data.api

import com.takseha.data.BuildConfig
import com.takseha.data.dto.LoginStart
import retrofit2.http.GET

interface GitudyApi {
    @GET(BuildConfig.LOGIN_PAGE_API)
    suspend fun getAllLoginStartData(): LoginStart
}