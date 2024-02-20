package com.takseha.presentation.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityLoginWebviewBinding

class LoginWebviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginWebviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_webview)
        setBinding()

        binding.loginWebview.loadUrl(intent.getStringExtra("url").toString())
    }

    private fun setBinding() {
        binding = ActivityLoginWebviewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}