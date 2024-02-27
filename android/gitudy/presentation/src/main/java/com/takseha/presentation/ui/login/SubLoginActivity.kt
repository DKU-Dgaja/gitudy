package com.takseha.presentation.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivitySubLoginBinding
import com.takseha.presentation.viewmodel.LoginViewModel


class SubLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_login)
        setBinding()

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.kakaoLoginBtn.setOnClickListener {
            startLogin("KAKAO")
        }
        binding.googleLoginBtn.setOnClickListener {

        }
    }

    private fun setBinding() {
        binding = ActivitySubLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun startLogin(platformType: String) {
        viewModel.startLogin(platformType)
        viewModel.loginPageUrl.observe(this, Observer {
            val intent = Intent(this, LoginWebViewActivity::class.java)
            intent.putExtra("url", it)
            intent.putExtra("platformType", platformType)
            startActivity(intent)
        })
    }
}
