package com.takseha.data.dto.feed


import com.google.gson.annotations.SerializedName

data class CheckRepoNameRequest(
    @SerializedName("name")
    val name: String
)