package com.takseha.data.dto.auth.register

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("github_id")
    val githubId: String = "",
    @SerializedName("fcm_token")
    val fcmToken: String = "",
    @SerializedName("push_alarm_yn")
    val pushAlarmYn: Boolean = true
)