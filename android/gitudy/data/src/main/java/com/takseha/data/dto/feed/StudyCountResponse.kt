package com.takseha.data.dto.feed

import com.google.gson.annotations.SerializedName

data class StudyCountResponse(
    @SerializedName("count")
    val count: Int
)
