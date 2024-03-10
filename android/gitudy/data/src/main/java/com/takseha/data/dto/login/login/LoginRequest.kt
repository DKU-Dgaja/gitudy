package com.takseha.data.dto.login.login

data class LoginRequest(
    val code: String,
    val state: String
)