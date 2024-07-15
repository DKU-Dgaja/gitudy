package com.takseha.data.dto.auth.login


import com.google.gson.annotations.SerializedName

data class LoginPageInfo(
    @SerializedName("platform_type")
    val platformType: String,
    @SerializedName("url")
    val url: String
)