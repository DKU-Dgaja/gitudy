package com.takseha.data.dto.auth.register


import com.google.gson.annotations.SerializedName

data class CheckNicknameRequest(
    @SerializedName("name")
    val name: String
)