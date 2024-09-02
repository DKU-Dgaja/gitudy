package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName

data class SocialInfo(
    @SerializedName("blog_link")
    val blogLink: String?,
    @SerializedName("github_link")
    val githubLink: String?,
    @SerializedName("linked_in_link")
    val linkedInLink: String?
)