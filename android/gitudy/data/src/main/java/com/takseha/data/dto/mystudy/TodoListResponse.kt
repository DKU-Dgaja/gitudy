package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName
import com.takseha.data.dto.feed.StudyListInfo

data class TodoListResponse(
    @SerializedName("res_code")
    val resCode: Int,
    @SerializedName("res_msg")
    val resMsg: String,
    @SerializedName("res_obj")
    val todoBody: TodoBody
)

data class TodoBody(
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