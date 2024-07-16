package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName

class StudyMemberResponse: ArrayList<StudyMember>()

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