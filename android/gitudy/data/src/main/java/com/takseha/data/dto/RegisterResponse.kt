package com.takseha.data.dto


import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("role")
    val role: String
)