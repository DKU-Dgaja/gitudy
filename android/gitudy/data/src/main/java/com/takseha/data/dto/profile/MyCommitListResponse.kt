package com.takseha.data.dto.profile


import com.google.gson.annotations.SerializedName
import com.takseha.data.dto.mystudy.Commit

data class MyCommitListResponse(
    @SerializedName("commit_info_list")
    val commitInfoList: List<Commit>,
    @SerializedName("cursor_idx")
    val cursorIdx: Int
)