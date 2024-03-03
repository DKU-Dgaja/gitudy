package com.takseha.data.dto


import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("githubId")
    val githubId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("platformId")
    val platformId: String,
    @SerializedName("platformType")
    val platformType: String,
    @SerializedName("role")
    val role: String
)