package com.takseha.data.dto.auth.login


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("res_msg")
    val resMsg: String,
    @SerializedName("res_obj")
    val tokenInfo: LoginTokenInfo
)

data class LoginTokenInfo(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("role")
    val role: String
)