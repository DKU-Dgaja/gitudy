package com.takseha.data.dto.mystudy

import com.google.gson.annotations.SerializedName

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
    val categoryNames: List<String>,
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
    @SerializedName("modified_date_time")
    val modifiedDateTime: String,
    @SerializedName("period_type")
    val periodType: String,
    @SerializedName("profile_image_url")
    val profileImageUrl: String,
    @SerializedName("score")
    val score: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("topic")
    val topic: String,
    @SerializedName("user_id")
    val userId: Int
)