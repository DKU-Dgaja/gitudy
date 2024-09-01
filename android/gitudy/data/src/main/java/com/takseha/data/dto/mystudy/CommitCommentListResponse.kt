package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName
import com.takseha.data.dto.auth.auth.UserInfoResponse

class CommitCommentListResponse : ArrayList<CommitComment>()

data class CommitComment(
    @SerializedName("content")
    val content: String,
    @SerializedName("created_date_time")
    val createdDateTime: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("modified_date_time")
    val modifiedDateTime: String,
    @SerializedName("my_comment")
    val myComment: Boolean,
    @SerializedName("study_commit_id")
    val studyCommitId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_info_response")
    val userInfoResponse: UserInfoResponse
)