package com.takseha.presentation.ui.login

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
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
            Log.d("LoginViewModel", "loginPageUrl: $it")
//            val loginIntent: CustomTabsIntent = CustomTabsIntent.Builder()
//                .build()
//            loginIntent.launchUrl(this, Uri.parse(it))
            val intent = Intent(this, LoginWebviewActivity::class.java)
            intent.putExtra("url", it)
            startActivity(intent)
        })
    }
}
