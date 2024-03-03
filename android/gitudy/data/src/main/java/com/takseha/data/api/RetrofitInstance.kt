package com.takseha.data.api

import com.takseha.data.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val client: Retrofit = Retrofit
        .Builder()
        .baseUrl(BuildConfig.GITUDY_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getInstance() : Retrofit {
        return client
    }
}