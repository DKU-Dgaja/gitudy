package com.takseha.data.dto.auth.register

import com.google.gson.annotations.SerializedName

data class NickNameResponse(
    @SerializedName("res_msg")
    val resMsg: String,
    @SerializedName("res_obj")
    val resObj: String
)
