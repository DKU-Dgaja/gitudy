package com.takseha.data.dto.mystudy

import com.google.gson.annotations.SerializedName
data class MakeTodoRequest(
    @SerializedName("detail")
    val detail: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("todo_date")
    val todoDate: String = "",
    @SerializedName("todo_link")
    val todoLink: String = ""
)