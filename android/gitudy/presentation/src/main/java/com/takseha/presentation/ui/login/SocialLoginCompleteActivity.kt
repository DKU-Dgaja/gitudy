package com.takseha.presentation.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivitySocialLoginCompleteBinding
class SocialLoginCompleteActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySocialLoginCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social_login_complete)
        setBinding()

        binding.confirmBtn.setOnClickListener {
            startActivity(Intent(this, PopupAgreementActivity::class.java))
        }
    }

    private fun setBinding() {
        binding = ActivitySocialLoginCompleteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}