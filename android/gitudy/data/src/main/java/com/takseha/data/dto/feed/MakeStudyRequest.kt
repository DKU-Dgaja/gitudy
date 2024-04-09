package com.takseha.data.dto.feed


import com.google.gson.annotations.SerializedName

data class MakeStudyRequest(
    @SerializedName("categories_id")
    val categoriesId: List<Int> = mutableListOf(),
    @SerializedName("info")
    val info: String = "",
    @SerializedName("maximum_member")
    val maximumMember: Int = 10,
    @SerializedName("period_type")
    val periodType: StudyPeriod = StudyPeriod.STUDY_PERIOD_WEEK,
    @SerializedName("profile_image_url")
    val profileImageUrl: String = "",
    @SerializedName("branch_name")
    val branchName: String = "",
    @SerializedName("status")
    val status: StudyStatus = StudyStatus.STUDY_PUBLIC,
    @SerializedName("topic")
    val topic: String = ""
)