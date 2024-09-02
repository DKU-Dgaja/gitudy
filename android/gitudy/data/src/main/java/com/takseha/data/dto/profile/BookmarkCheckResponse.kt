package com.takseha.data.dto.profile


import com.google.gson.annotations.SerializedName
import com.takseha.data.dto.auth.auth.UserInfoResponse
import com.takseha.data.dto.mystudy.StudyInfoResponse

data class BookmarkCheckResponse(
    @SerializedName("my_bookmark")
    val myBookmark: Boolean
)