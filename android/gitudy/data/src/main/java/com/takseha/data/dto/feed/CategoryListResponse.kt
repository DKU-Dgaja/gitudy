package com.takseha.data.dto.feed


import com.google.gson.annotations.SerializedName

class CategoryListResponse: ArrayList<Category>()

data class Category(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)