package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName

data class StudyCommentListResponse(
    @SerializedName("cursor_idx")
    val cursorIdx: Int,
    @SerializedName("study_comment_list")
    val studyCommentList: List<Comment>
)