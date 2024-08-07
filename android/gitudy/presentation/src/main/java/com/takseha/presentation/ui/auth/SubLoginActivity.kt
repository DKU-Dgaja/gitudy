package com.takseha.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivitySubLoginBinding
import com.takseha.presentation.viewmodel.auth.LoginViewModel


class SubLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_login)
        window.statusBarColor = ContextCompat.getColor(this, R.color.BACKGROUND)
        setBinding()

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        with(binding) {
            kakaoLoginBtn.setOnClickListener {
                startLogin("KAKAO")
            }
            googleLoginBtn.setOnClickListener {
                startLogin("GOOGLE")
            }
        }
    }

    private fun setBinding() {
        binding = ActivitySubLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun startLogin(platformType: String) {
        viewModel.startLogin(platformType)
        viewModel.loginPageUrl.observe(this) {
            val intent = Intent(this, LoginWebViewActivity::class.java)
            intent.putExtra("url", it)
            intent.putExtra("platformType", platformType)
            startActivity(intent)
        }
    }
}
