package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName

data class SetConventionRequest(
    @SerializedName("active")
    val active: Boolean = true,
    @SerializedName("content")
    val content: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("name")
    val name: String = ""
)