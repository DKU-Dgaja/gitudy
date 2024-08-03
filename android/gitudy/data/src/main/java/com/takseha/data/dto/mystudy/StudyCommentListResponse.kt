package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName
import com.takseha.data.dto.feed.UserInfo

data class StudyCommentListResponse(
    @SerializedName("cursor_idx")
    val cursorIdx: Int,
    @SerializedName("study_comment_list")
    val studyCommentList: List<StudyComment>
)

data class StudyComment(
    @SerializedName("content")
    val content: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("study_info_id")
    val studyInfoId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_info_response")
    val userInfo: UserInfo
)