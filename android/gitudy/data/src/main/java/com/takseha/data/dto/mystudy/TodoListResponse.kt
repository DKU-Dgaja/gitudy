package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName

data class TodoListResponse(
    @SerializedName("cursor_idx")
    val cursorIdx: Int,
    @SerializedName("todo_list")
    val todoList: List<Todo>
)

data class Todo(
    @SerializedName("detail")
    val detail: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("study_info_id")
    val studyInfoId: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("todo_date")
    val todoDate: String,
    @SerializedName("todo_link")
    val todoLink: String
)