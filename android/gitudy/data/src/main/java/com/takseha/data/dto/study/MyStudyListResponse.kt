package com.takseha.data.dto.study

import com.google.gson.annotations.SerializedName

data class MyStudyListResponse(
    @SerializedName("res_code")
    val resCode: Int,
    @SerializedName("res_msg")
    val resMsg: String,
    @SerializedName("res_obj")
    val myStudyList: List<MyStudy>
)

data class MyStudy(
    @SerializedName("createdDateTime")
    val createdDateTime: String,
    @SerializedName("currentMember")
    val currentMember: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("info")
    val info: String,
    @SerializedName("lastCommitDay")
    val lastCommitDay: String,
    @SerializedName("maximumMember")
    val maximumMember: Int,
    @SerializedName("periodType")
    val periodType: String,
    @SerializedName("profileImageUrl")
    val profileImageUrl: String,
    @SerializedName("score")
    val score: Int,
    @SerializedName("topic")
    val topic: String,
    @SerializedName("userId")
    val userId: Int
)