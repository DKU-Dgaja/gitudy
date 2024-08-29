package com.takseha.data.dto.feed

import com.google.gson.annotations.SerializedName

data class StudyListResponse(
    @SerializedName("study_info_list")
    val studyInfoList: List<StudyInfo>,
    @SerializedName("cursor_idx")
    val cursorIdx: Long?,
    @SerializedName("study_category_mapping_map")
    val studyCategoryMappingMap: Map<Int, List<String>>
)

data class StudyInfo(
    @SerializedName("created_date_time")
    val createdDateTime: String,
    @SerializedName("current_member")
    val currentMember: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("info")
    val info: String,
    @SerializedName("is_leader")
    val isLeader: Boolean,
    @SerializedName("last_commit_day")
    val lastCommitDay: String?,
    @SerializedName("maximum_member")
    val maximumMember: Int,
    @SerializedName("period_type")
    val periodType: StudyPeriodStatus,
    @SerializedName("profile_image_url")
    val profileImageUrl: String,
    @SerializedName("score")
    val score: Int,
    @SerializedName("status")
    val status: StudyStatus,
    @SerializedName("topic")
    val topic: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_info")
    val userInfo: List<UserInfo>
)

data class UserInfo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("profile_image_url")
    val profileImageUrl: String
)