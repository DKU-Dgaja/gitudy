package com.takseha.presentation.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.takseha.data.dto.auth.login.RoleStatus
import com.takseha.presentation.firebase.MyFirebaseMessagingService
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivitySocialLoginCompleteBinding
import com.takseha.presentation.ui.home.MainHomeActivity

class SocialLoginCompleteActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySocialLoginCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social_login_complete)
        window.statusBarColor = ContextCompat.getColor(this, R.color.BACKGROUND)
        setBinding()

        binding.confirmBtn.setOnClickListener {
            val role = intent.getStringExtra("role").toString()
            Log.d("SocialLoginCompleteActivity", "role: $role")
            controlRegisterView(role)
        }
    }

    private fun setBinding() {
        binding = ActivitySocialLoginCompleteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun controlRegisterView(role: String) {
        try {
            when (RoleStatus.valueOf(role)) {
                RoleStatus.UNAUTH, RoleStatus.WITHDRAW -> {
                    startActivity(Intent(this, PopupAgreementActivity::class.java))
                }
                else -> {
                    startActivity(Intent(this, MainHomeActivity::class.java))
                }
            }
        } catch (e: IllegalArgumentException) {
            // role == null일 때
            Log.e("SocialLoginCompleteActivity", e.message.toString())
            startActivity(Intent(this, PopupAgreementActivity::class.java))
        }
    }
}