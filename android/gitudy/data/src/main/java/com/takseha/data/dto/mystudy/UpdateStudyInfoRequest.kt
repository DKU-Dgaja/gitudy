package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName

data class UpdateStudyInfoRequest(
    @SerializedName("categories_id")
    val categoriesId: List<Int>,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("info")
    val info: String,
    @SerializedName("maximum_member")
    val maximumMember: Int,
    @SerializedName("period_type")
    val periodType: String,
    @SerializedName("profile_image_url")
    val profileImageUrl: String,
    @SerializedName("repository_info")
    val repositoryInfo: RepositoryInfo,
    @SerializedName("status")
    val status: String,
    @SerializedName("topic")
    val topic: String
)