package com.takseha.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.takseha.presentation.R
import com.takseha.presentation.ui.auth.LoginActivity
import com.takseha.presentation.ui.home.MainHomeActivity
import com.takseha.presentation.viewmodel.auth.SplashViewModel
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()

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
        Handler(Looper.getMainLooper()).postDelayed({
            if (availableTokenState) {
                startActivity(Intent(this, MainHomeActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 2000)
    }
}