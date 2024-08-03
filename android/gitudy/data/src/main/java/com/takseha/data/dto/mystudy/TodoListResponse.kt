package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName

data class TodoListResponse(
    @SerializedName("todo_list")
    val todoList: List<Todo>,
    @SerializedName("cursor_idx")
    val cursorIdx: Int
)

data class Todo(
    @SerializedName("detail")
    val detail: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("study_info_id")
    val studyInfoId: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("todo_date")
    val todoDate: String = "",
    @SerializedName("todo_code")
    val todoCode: String = "",
    @SerializedName("todo_link")
    val todoLink: String = "",
    @SerializedName("created_date_time")
    val todoSetDate: String = "",
    @SerializedName("commits")
    val commitList: List<Commit> = listOf()
)