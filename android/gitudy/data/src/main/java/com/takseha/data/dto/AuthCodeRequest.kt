package com.takseha.data.dto

data class AuthCodeRequest(
    val code: String,
    val state: String
)