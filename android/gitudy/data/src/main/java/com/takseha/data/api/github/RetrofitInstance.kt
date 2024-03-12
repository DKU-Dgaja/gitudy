package com.takseha.data.api.github

import com.takseha.data.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    private val client: Retrofit = Retrofit
        .Builder()
        .baseUrl(BuildConfig.GITHUB_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getInstance() : Retrofit {
        return client
    }
}