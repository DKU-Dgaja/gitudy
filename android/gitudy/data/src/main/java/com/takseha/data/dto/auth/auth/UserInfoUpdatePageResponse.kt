package com.takseha.data.dto.auth.auth


import com.google.gson.annotations.SerializedName
import com.takseha.data.dto.mystudy.SocialInfo

data class UserInfoUpdatePageResponse(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("profile_image_url")
    val profileImageUrl: String = "",
    @SerializedName("profile_public_yn")
    val profilePublicYn: Boolean? = null,
    @SerializedName("social_info")
    val socialInfo: SocialInfo? = null
)