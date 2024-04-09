package com.takseha.presentation.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.takseha.presentation.R
import com.takseha.presentation.ui.auth.LoginActivity

class SplashActivity : AppCompatActivity() {
    /*
    TODO :
     리팩토링 시 구현해야 할 것
     1. splash 화면에서 info api 연동해서 role 확인 -> 자동로그인 기능 구현(로그인 및 회원가입 화면 pass 하는 로직 구현)
     2. 닉네임 특수기호(이모티콘) 입력 불가하도록 하는 기능 추가
     3. githubId 유효성 검사
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.statusBarColor = ContextCompat.getColor(this, R.color.BACKGROUND)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 2000)
    }
}