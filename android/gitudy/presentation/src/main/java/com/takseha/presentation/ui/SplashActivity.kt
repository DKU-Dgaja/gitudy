package com.takseha.presentation.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.takseha.presentation.R
import com.takseha.presentation.ui.auth.LoginActivity
import com.takseha.presentation.ui.home.MainHomeActivity
import com.takseha.presentation.viewmodel.auth.SplashViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()
    /*
    TODO :
     리팩토링 시 구현해야 할 것
     1. splash 화면에서 info api 연동해서 role 확인 -> 자동로그인 기능 구현(로그인 및 회원가입 화면 pass 하는 로직 구현)
     2. 닉네임 특수기호(이모티콘) 입력 불가하도록 하는 기능 추가
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.statusBarColor = ContextCompat.getColor(this, R.color.BACKGROUND)

        lifecycleScope.launch {
            viewModel.checkAvailableToken()
        }
        lifecycleScope.launch {
            viewModel.availableTokenCheck.observe(this@SplashActivity) {
                setMainHome(it)
                Log.d("SplashActivity", it.toString())
            }
        }
    }

    private fun setMainHome(availableTokenState: Boolean) {
        if (availableTokenState) {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, MainHomeActivity::class.java))
                finish()
            }, 2000)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }, 2000)
        }
    }
}