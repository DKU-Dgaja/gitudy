package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName

data class TodoProgressResponse(
    @SerializedName("complete_member_count")
    val completeMemberCount: Int,
    @SerializedName("todo_id")
    val todoId: Int,
    @SerializedName("total_member_count")
    val totalMemberCount: Int
)