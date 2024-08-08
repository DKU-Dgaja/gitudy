package com.takseha.data.dto.mystudy

import com.google.gson.annotations.SerializedName

data class Todo(
    @SerializedName("detail")
    val detail: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("study_info_id")
    val studyInfoId: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("todo_date")
    val todoDate: String = "",
    @SerializedName("todo_folder_name")
    val todoFolderName: String = "",
    @SerializedName("todo_link")
    val todoLink: String = "",
    @SerializedName("created_date_time")
    val todoSetDate: String = "",
    @SerializedName("commits")
    val commitList: List<Commit> = listOf()
)
