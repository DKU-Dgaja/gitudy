package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName
import com.takseha.data.dto.auth.login.RoleStatus

class StudyMemberListResponse: ArrayList<StudyMember>()

data class StudyMember(
    @SerializedName("name")
    val name: String,
    @SerializedName("profile_image_url")
    val profileImageUrl: String,
    @SerializedName("role")
    val role: RoleStatus,
    @SerializedName("score")
    val score: Int,
    @SerializedName("status")
    val status: StudyApplyStatus,
    @SerializedName("user_id")
    val userId: Int
)