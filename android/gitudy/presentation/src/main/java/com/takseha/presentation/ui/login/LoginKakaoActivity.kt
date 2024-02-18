package com.takseha.presentation.ui.login

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityLoginKakaoBinding

class LoginKakaoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginKakaoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_kakao)
        setBinding()

        val intentKakaoLogin: CustomTabsIntent = CustomTabsIntent.Builder()
            .build()
        intentKakaoLogin.launchUrl(this, Uri.parse(intent.getStringExtra("url").toString()))
    }

    private fun setBinding() {
        binding = ActivityLoginKakaoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}