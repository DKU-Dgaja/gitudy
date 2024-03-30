package com.takseha.data.dto.feed


import com.google.gson.annotations.SerializedName

data class MakeStudyRequest(
    @SerializedName("categories_id")
    val categoriesId: List<Int> = mutableListOf(),
    @SerializedName("end_date")
    val endDate: String = "",
    @SerializedName("info")
    val info: String = "",
    @SerializedName("maximum_member")
    val maximumMember: Int = 10,
    @SerializedName("period_type")
    val periodType: String = StudyPeriod.STUDY_PERIOD_WEEK.toString(),
    @SerializedName("profile_image_url")
    val profileImageUrl: String = "",
    @SerializedName("repository_info")
    val repositoryInfo: RepositoryInfo = RepositoryInfo(),
    @SerializedName("status")
    val status: String = "",
    @SerializedName("topic")
    val topic: String = "",
    @SerializedName("user_id")
    val userId: Int = 0
)

data class RepositoryInfo(
    @SerializedName("branch_name")
    val branchName: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("owner")
    val owner: String = ""
)