package com.takseha.data.dto.auth.register


import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("res_code")
    val resCode: Int,
    @SerializedName("res_msg")
    val resMsg: String,
    @SerializedName("res_obj")
    val tokenInfo: RegisterTokenInfo
)

data class RegisterTokenInfo(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("role")
    val role: String
)