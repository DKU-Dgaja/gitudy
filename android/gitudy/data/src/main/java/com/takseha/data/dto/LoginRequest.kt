package com.takseha.data.dto

data class LoginRequest(
    val code: String,
    val state: String
)