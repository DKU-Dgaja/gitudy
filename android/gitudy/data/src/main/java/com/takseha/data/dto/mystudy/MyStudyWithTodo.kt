package com.takseha.data.dto.mystudy

import com.takseha.data.dto.feed.StudyPeriod

data class MyStudyWithTodo(
    val createdDateTime: String,
    val currentMember: Int,
    val id: Int,
    val info: String,
    val lastCommitDay: String,
    val maximumMember: Int,
    val periodType: StudyPeriod,
    val profileImageUrl: String,
    val score: Int,
    val topic: String,
    val userId: Int,
    val todoTitle: String,
    val todoTime: String,
    val todoCheck: String, // enum
    val totalNum: Int,
    val todoCheckNum: Int
)

