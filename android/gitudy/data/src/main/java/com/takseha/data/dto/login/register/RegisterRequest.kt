package com.takseha.data.dto.login.register


import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("githubId")
    val githubId: String,
    @SerializedName("name")
    val name: String
)