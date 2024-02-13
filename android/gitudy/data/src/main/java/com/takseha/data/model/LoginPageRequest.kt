package com.takseha.data.model

import com.google.gson.JsonObject

data class LoginPageRequest(
    val res_code: Int,
    val res_msg: String,
    val res_obj: JsonObject
)