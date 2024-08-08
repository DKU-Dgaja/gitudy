package com.takseha.data.repository.category

import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.data.api.gitudy.category.GitudyCategoryService
import com.takseha.data.api.gitudy.member.GitudyMemberService
import com.takseha.data.dto.feed.MessageRequest

class GitudyCategoryRepository {
    private val client = RetrofitInstance.getInstance().create(GitudyCategoryService::class.java)

    suspend fun getAllCategory(
    ) = client.getAllCategory()
}
