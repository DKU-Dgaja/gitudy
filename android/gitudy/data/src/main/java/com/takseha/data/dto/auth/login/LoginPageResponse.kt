package com.takseha.data.dto.auth.login


import com.google.gson.annotations.SerializedName

data class LoginPageResponse(
    @SerializedName("res_code")
    val resCode: Int,
    @SerializedName("res_msg")
    val resMsg: String,
    @SerializedName("res_obj")
    val resObj: List<LoginPageInfo>
)

data class LoginPageInfo(
    @SerializedName("platform_type")
    val platformType: String,
    @SerializedName("url")
    val url: String
)