package com.takseha.data.dto.mystudy

import com.google.gson.annotations.SerializedName
import com.takseha.data.dto.feed.StudyPeriod
import com.takseha.data.dto.feed.StudyStatus

data class MyStudyResponse(
    @SerializedName("res_code")
    val resCode: Int,
    @SerializedName("res_msg")
    val resMsg: String,
    @SerializedName("res_obj")
    val myStudyInfo: MyStudyInfo
)

data class MyStudyInfo(
    @SerializedName("category_names")
    val categoryNames: List<String> = listOf(),
    @SerializedName("created_date_time")
    val createdDateTime: String = "",
    @SerializedName("current_member")
    val currentMember: Int = 0,
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("info")
    val info: String = "",
    @SerializedName("last_commit_day")
    val lastCommitDay: String = "",
    @SerializedName("maximum_member")
    val maximumMember: Int = 0,
    @SerializedName("modified_date_time")
    val modifiedDateTime: String = "",
    @SerializedName("period_type")
    val periodType: StudyPeriod = StudyPeriod.STUDY_PERIOD_WEEK,
    @SerializedName("profile_image_url")
    val profileImageUrl: String = "",
    @SerializedName("score")
    val score: Int = 0,
    @SerializedName("status")
    val status: StudyStatus = StudyStatus.STUDY_PRIVATE,
    @SerializedName("topic")
    val topic: String = "",
    @SerializedName("user_id")
    val userId: Int = 0,
    @SerializedName("repository_info")
    val githubLinkInfo: RepositoryInfo = RepositoryInfo()
)

data class RepositoryInfo(
    @SerializedName("owner")
    val owner: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("branch_name")
    val branchName: String = ""
)