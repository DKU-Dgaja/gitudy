package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName

data class CommitCommentRequest(
    @SerializedName("content")
    val content: String,
    @SerializedName("study_info_id")
    val studyInfoId: Int
)