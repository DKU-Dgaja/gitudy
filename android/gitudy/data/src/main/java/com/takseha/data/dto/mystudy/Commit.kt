package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName

data class Commit(
    @SerializedName("commit_date")
    val commitDate: String,
    @SerializedName("commit_sha")
    val commitSha: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("like_count")
    val likeCount: LikeCount,
    @SerializedName("message")
    val message: String,
    @SerializedName("rejection-reason")
    val rejectionReason: String,
    @SerializedName("status")
    val status: CommitStatus,
    @SerializedName("study_info_id")
    val studyInfoId: Int,
    @SerializedName("study_todo_id")
    val studyTodoId: Int,
    @SerializedName("user_id")
    val userId: Int
)

data class LikeCount(
    @SerializedName("like_count")
    val likeCount: Int
)