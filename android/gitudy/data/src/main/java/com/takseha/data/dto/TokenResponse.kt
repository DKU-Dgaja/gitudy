package com.takseha.data.dto


import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("res_code")
    val resCode: Int,
    @SerializedName("res_msg")
    val resMsg: String,
    @SerializedName("res_obj")
    val tokenInfo: TokenInfo
)

data class TokenInfo(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("role")
    val role: String
)