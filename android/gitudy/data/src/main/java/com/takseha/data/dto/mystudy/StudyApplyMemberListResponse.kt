package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName

data class StudyApplyMemberListResponse(
    @SerializedName("apply_list")
    val applyList: List<StudyApplyMemberList>,
    @SerializedName("cursor_idx")
    val cursorIdx: Int
)

data class StudyApplyMemberList(
    @SerializedName("github_id")
    val githubId: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("point")
    val point: Int,
    @SerializedName("profile_image_url")
    val profileImageUrl: String,
    @SerializedName("profile_public_yn")
    val profilePublicYn: Boolean,
    @SerializedName("score")
    val score: Int,
    @SerializedName("sign_greeting")
    val signGreeting: String,
    @SerializedName("social_info")
    val socialInfo: SocialInfo,
    @SerializedName("user_id")
    val userId: Int
)