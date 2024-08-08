package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName

data class TodoListResponse(
    @SerializedName("todo_list")
    val todoList: List<Todo>,
    @SerializedName("cursor_idx")
    val cursorIdx: Int
)