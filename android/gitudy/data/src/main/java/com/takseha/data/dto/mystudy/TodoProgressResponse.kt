package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName

data class TodoProgressResponse(
    @SerializedName("res_code")
    val resCode: Int,
    @SerializedName("res_msg")
    val resMsg: String,
    @SerializedName("res_obj")
    val todoProgress: TodoProgress
)

data class TodoProgress(
    @SerializedName("complete_member_count")
    val completeMemberCount: Int,
    @SerializedName("todo_id")
    val todoId: Int,
    @SerializedName("total_member_count")
    val totalMemberCount: Int
)