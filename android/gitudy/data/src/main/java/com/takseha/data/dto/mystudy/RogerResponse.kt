package com.takseha.data.dto.mystudy

import com.google.gson.annotations.SerializedName

data class RogerResponse(
    @SerializedName("res_code")
    val resCode: Int,
    @SerializedName("res_msg")
    val resMsg: String,
    @SerializedName("res_obj")
    val resObj: String
)
