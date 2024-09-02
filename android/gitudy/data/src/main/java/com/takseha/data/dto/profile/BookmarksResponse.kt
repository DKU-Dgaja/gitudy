package com.takseha.data.dto.profile


import com.google.gson.annotations.SerializedName
import com.takseha.data.dto.auth.auth.UserInfoResponse
import com.takseha.data.dto.mystudy.StudyInfoResponse

data class BookmarksResponse(
    @SerializedName("bookmark_info_list")
    val bookmarkInfoList: List<Bookmark>,
    @SerializedName("cursor_idx")
    val cursorIdx: Long
)

data class Bookmark(
    @SerializedName("id")
    val id: Int,
    @SerializedName("study_info_id")
    val studyInfoId: Int,
    @SerializedName("study_info_with_id_response")
    val studyInfoWithIdResponse: StudyInfoResponse,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_info_response")
    val userInfoResponse: UserInfoResponse
)

//data class StudyInfoWithIdResponse(
//    @SerializedName("current_member")
//    val currentMember: Int,
//    @SerializedName("end_date")
//    val endDate: String,
//    @SerializedName("id")
//    val id: Int,
//    @SerializedName("info")
//    val info: String,
//    @SerializedName("last_commit_day")
//    val lastCommitDay: String,
//    @SerializedName("maximum_member")
//    val maximumMember: Int,
//    @SerializedName("notice")
//    val notice: String,
//    @SerializedName("profile_image_url")
//    val profileImageUrl: String,
//    @SerializedName("repository_info")
//    val repositoryInfo: RepositoryInfo,
//    @SerializedName("score")
//    val score: Int,
//    @SerializedName("status")
//    val status: String,
//    @SerializedName("topic")
//    val topic: String
//)