package com.takseha.data.repository.gitudy

import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.api.gitudy.GitudyCategoryService

class GitudyCategoryRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyCategoryService::class.java)

    suspend fun getAllCategory(
    ) = client.getAllCategory()

    suspend fun getStudyCategory(
        studyInfoId: Int
    ) = client.getStudyCategory(studyInfoId, null, 20)
}
