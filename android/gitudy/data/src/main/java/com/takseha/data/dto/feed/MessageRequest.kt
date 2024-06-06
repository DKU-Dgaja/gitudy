package com.takseha.data.dto.feed


import com.google.gson.annotations.SerializedName

data class MessageRequest(
    @SerializedName("message")
    val message: String
)