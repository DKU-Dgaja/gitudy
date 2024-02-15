package com.takseha.data.dto


import com.google.gson.annotations.SerializedName

data class LoginPageDetail(
    @SerializedName("platform_type")
    val platformType: String,
    @SerializedName("url")
    val url: String
)