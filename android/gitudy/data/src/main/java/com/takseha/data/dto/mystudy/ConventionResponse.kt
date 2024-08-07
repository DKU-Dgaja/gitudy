package com.takseha.data.dto.mystudy


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ConventionResponse(
    @SerializedName("cursor_idx")
    val cursorIdx: Int,
    @SerializedName("study_convention_list")
    val studyConventionList: List<StudyConvention>
)

data class StudyConvention(
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("content")
    val content: String,
    @SerializedName("convention_id")
    val conventionId: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("study_info_id")
    val studyInfoId: Int
) : Serializable