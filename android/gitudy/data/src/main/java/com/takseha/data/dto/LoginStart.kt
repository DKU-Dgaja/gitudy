package com.takseha.data.dto


import com.google.gson.annotations.SerializedName

data class LoginStart(
    @SerializedName("res_code")
    val resCode: Int,
    @SerializedName("res_msg")
    val resMsg: String,
    @SerializedName("res_obj")
    val resObj: List<LoginPageDetail>
)