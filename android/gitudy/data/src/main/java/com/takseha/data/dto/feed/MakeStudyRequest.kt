package com.takseha.data.dto.feed


import com.google.gson.annotations.SerializedName
import com.takseha.data.dto.mystudy.RepositoryInfo

data class MakeStudyRequest(
    @SerializedName("categories_id")
    val categoriesId: List<Int> = mutableListOf(),
    @SerializedName("info")
    val info: String = "",
    @SerializedName("maximum_member")
    val maximumMember: Int = 10,
    @SerializedName("period_type")
    val periodType: StudyPeriodStatus = StudyPeriodStatus.STUDY_PERIOD_WEEK,
    @SerializedName("profile_image_url")
    val profileImageUrl: String = "",
    @SerializedName("repository_name")
    val repositoryName: String = "",
    @SerializedName("status")
    val status: StudyStatus = StudyStatus.STUDY_PUBLIC,
    @SerializedName("topic")
    val topic: String = ""
)