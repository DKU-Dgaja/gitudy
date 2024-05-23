package com.takseha.data.dto.feed

import com.google.gson.annotations.SerializedName

data class StudyListResponse(
    @SerializedName("res_code")
    val resCode: Int,
    @SerializedName("res_msg")
    val resMsg: String,
    @SerializedName("res_obj")
    val studyListInfo: StudyListInfo
)

data class StudyListInfo(
    @SerializedName("cursor_idx")
    val cursorIdx: Long?,
    @SerializedName("study_category_mapping_map")
    val studyCategoryMappingMap: StudyCategoryMappingMap,
    @SerializedName("study_info_list")
    val studyInfoList: List<StudyInfo>,
    @SerializedName("study_user_info_map")
    val studyUserInfoMap: StudyUserInfoMap
)

data class StudyCategoryMappingMap(
    @SerializedName("additionalProp1")
    val additionalProp1: List<String>,
    @SerializedName("additionalProp2")
    val additionalProp2: List<String>,
    @SerializedName("additionalProp3")
    val additionalProp3: List<String>
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
    @SerializedName("last_commit_day")
    val lastCommitDay: String,
    @SerializedName("maximum_member")
    val maximumMember: Int,
    @SerializedName("period_type")
    val periodType: StudyPeriod,
    @SerializedName("profile_image_url")
    val profileImageUrl: String,
    @SerializedName("score")
    val score: Int,
    @SerializedName("topic")
    val topic: String,
    @SerializedName("user_id")
    val userId: Int
)

data class StudyUserInfoMap(
    @SerializedName("additionalProp1")
    val additionalProp1: List<AdditionalProp>,
    @SerializedName("additionalProp2")
    val additionalProp2: List<AdditionalProp>,
    @SerializedName("additionalProp3")
    val additionalProp3: List<AdditionalProp>
)

data class AdditionalProp(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("profile_image_url")
    val profileImageUrl: String
)