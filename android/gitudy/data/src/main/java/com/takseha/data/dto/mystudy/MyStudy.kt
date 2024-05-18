package com.takseha.data.dto.mystudy

data class MyStudy(
    val currentMember: Int,
    val id: Int,
    val todoTitle: String,
    val todoTime: String,
    val todoCheck: String, // enum
    val totalNum: Int,
    val todoCheckNum: Int,
    val score: Int,
    val topic: String
)

