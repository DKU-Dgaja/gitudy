package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName

data class CommitRejectRequest(
    @SerializedName("rejection_reason")
    val rejectionReason: String
)