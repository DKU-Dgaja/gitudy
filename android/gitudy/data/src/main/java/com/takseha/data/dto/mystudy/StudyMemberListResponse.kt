package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName

data class StudyMemberListResponse(
    @SerializedName("res_code")
    val resCode: Int,
    @SerializedName("res_msg")
    val resMsg: String,
    @SerializedName("res_obj")
    val studyMemberList: List<StudyMember>
)

data class StudyMember(
    @SerializedName("name")
    val name: String,
    @SerializedName("profile_image_url")
    val profileImageUrl: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("score")
    val score: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("user_id")
    val userId: Int
)