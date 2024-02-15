package com.takseha.data.repository

import com.takseha.data.api.GitudyApi
import com.takseha.data.api.RetrofitInstance

class Repository {
    private val client = RetrofitInstance.getInstance().create(GitudyApi::class.java)
    suspend fun getAllLoginStartData() = client.getAllLoginStartData()
}