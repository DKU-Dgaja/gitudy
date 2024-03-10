package com.takseha.data.dto.auth.login

data class LoginRequest(
    val code: String,
    val state: String
)