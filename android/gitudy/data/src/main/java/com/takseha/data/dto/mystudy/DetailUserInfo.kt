package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName

data class DetailUserInfo(
    @SerializedName("github_id")
    val githubId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("point")
    val point: Int,
    @SerializedName("profile_image_url")
    val profileImageUrl: String,
    @SerializedName("profile_public_yn")
    val profilePublicYn: Boolean,
    @SerializedName("push_alarm_yn")
    val pushAlarmYn: Boolean,
    @SerializedName("role")
    val role: String,
    @SerializedName("score")
    val score: Int,
    @SerializedName("user_id")
    val userId: Int
)