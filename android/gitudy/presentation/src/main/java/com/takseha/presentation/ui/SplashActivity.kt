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
import com.takseha.data.sharedPreferences.SP
import com.takseha.data.sharedPreferences.SPKey
import com.takseha.presentation.R
import com.takseha.presentation.firebase.MyFirebaseMessagingService
import com.takseha.presentation.ui.auth.LoginActivity
import com.takseha.presentation.ui.home.MainHomeActivity
import com.takseha.presentation.viewmodel.auth.SplashViewModel
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()
    private lateinit var prefs: SP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        prefs = SP(baseContext)
        window.statusBarColor = ContextCompat.getColor(this, R.color.BACKGROUND)

        lifecycleScope.launch {
            viewModel.checkAvailableToken()
            val fcmToken = MyFirebaseMessagingService.getFirebaseToken().toString()
            Log.d("SplashActivity", "access token: ${prefs.loadPref(SPKey.ACCESS_TOKEN, "0")}\nrefresh token: ${prefs.loadPref(SPKey.REFRESH_TOKEN, "0")}\nfcm token: $fcmToken")
        }

        viewModel.availableTokenCheck.observe(this@SplashActivity) {
            setMainHome(it)
            Log.d("SplashActivity", it.toString())
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