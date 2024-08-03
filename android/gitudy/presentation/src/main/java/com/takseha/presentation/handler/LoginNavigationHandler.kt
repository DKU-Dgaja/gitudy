package com.takseha.presentation.handler

import android.content.Context
import android.content.Intent
import com.takseha.data.token.NavigationHandler
import com.takseha.presentation.ui.auth.LoginActivity

class LoginNavigationHandler(private val context: Context) : NavigationHandler {
    override fun navigateToLogin() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }
}