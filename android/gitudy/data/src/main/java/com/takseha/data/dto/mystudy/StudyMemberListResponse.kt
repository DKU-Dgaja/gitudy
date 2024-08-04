package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName
import com.takseha.data.dto.auth.login.RoleStatus

class StudyMemberListResponse: ArrayList<StudyMember>()

data class StudyMember(
    @SerializedName("role")
    val role: RoleStatus,
    @SerializedName("score")
    val score: Int,
    @SerializedName("status")
    val status: StudyApplyStatus,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_info")
    val userInfo: DetailUserInfo
)