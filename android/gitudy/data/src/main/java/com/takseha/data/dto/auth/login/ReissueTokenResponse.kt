package com.takseha.data.dto.auth.login


import com.google.gson.annotations.SerializedName

data class ReissueTokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)