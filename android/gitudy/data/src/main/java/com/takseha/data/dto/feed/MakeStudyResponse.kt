package com.takseha.data.dto.feed


import com.google.gson.annotations.SerializedName

data class MakeStudyResponse(
    @SerializedName("res_code")
    val resCode: Int,
    @SerializedName("res_msg")
    val resMsg: String,
    @SerializedName("res_obj")
    val resObj: String
)