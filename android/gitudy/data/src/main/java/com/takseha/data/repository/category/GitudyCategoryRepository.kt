package com.takseha.data.repository.category

import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.api.gitudy.category.GitudyCategoryService

class GitudyCategoryRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyCategoryService::class.java)

    suspend fun getAllCategory(
    ) = client.getAllCategory()

    suspend fun getStudyCategory(
        studyInfoId: Int
    ) = client.getStudyCategory(studyInfoId, null, 20)
}
