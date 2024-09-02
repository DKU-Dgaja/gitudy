package com.takseha.data.dto.feed


import com.google.gson.annotations.SerializedName

data class StudyRankResponse(
    @SerializedName("ranking")
    val ranking: Int,
    @SerializedName("score")
    val score: Int
)