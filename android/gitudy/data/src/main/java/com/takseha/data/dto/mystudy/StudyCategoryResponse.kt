package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName
import com.takseha.data.dto.feed.Category

data class StudyCategoryResponse(
    @SerializedName("category_response_list")
    val categoryResponseList: List<Category>,
    @SerializedName("cursor_idx")
    val cursorIdx: Int
)