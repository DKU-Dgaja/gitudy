package com.takseha.data.dto.home


import com.google.gson.annotations.SerializedName

class NoticeResponse: ArrayList<Notice>()

data class Notice(
    @SerializedName("id")
    val id: String,
    @SerializedName("local_date_time")
    val localDateTime: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("study_info_id")
    val studyInfoId: Int,
    @SerializedName("title")
    val title: String
)