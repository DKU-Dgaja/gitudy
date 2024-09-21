package com.takseha.data.dto.mystudy

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("content")
    val content: String,
    @SerializedName("created_date_time")
    val commentSetDate: String,
    @SerializedName("my_comment")
    val isMyComment: Boolean,
    @SerializedName("id")
    val id: Int,
    @SerializedName("study_info_id")
    val studyInfoId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_info_response")
    val userInfo: DetailUserInfo
)
