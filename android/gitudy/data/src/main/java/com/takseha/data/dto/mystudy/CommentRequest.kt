package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName

data class CommentRequest(
    @SerializedName("content")
    val content: String
)