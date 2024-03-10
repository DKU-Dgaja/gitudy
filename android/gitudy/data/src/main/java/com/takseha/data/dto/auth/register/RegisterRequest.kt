package com.takseha.data.dto.auth.register


import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("githubId")
    val githubId: String,
    @SerializedName("name")
    val name: String
)