package com.takseha.data.dto.mystudy

import com.takseha.data.dto.feed.StudyInfo

data class MyStudyWithTodo(
    val studyImg: String,
    val studyInfo: StudyInfo,
    val todoTitle: String?,
    val todoTime: String?,
    val todoCheck: TodoStatus?,
    val todoCheckNum: Int?
)

