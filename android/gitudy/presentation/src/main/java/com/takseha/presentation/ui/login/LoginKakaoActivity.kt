package com.takseha.presentation.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityLoginBinding
import com.takseha.presentation.databinding.ActivityLoginKakaoBinding
import com.takseha.presentation.databinding.ActivitySubLoginBinding

class LoginKakaoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginKakaoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_kakao)
        setBinding()

        binding.kakaoLoginWebView.loadUrl(intent.getStringExtra("url").toString())
    }

    private fun setBinding() {
        binding = ActivityLoginKakaoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}