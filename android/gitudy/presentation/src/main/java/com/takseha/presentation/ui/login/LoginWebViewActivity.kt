package com.takseha.presentation.ui.login

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import com.takseha.common.model.SharedPreferencesKey
import com.takseha.common.util.SharedPreferences
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityLoginWebviewBinding

class LoginWebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginWebviewBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_webview)
        setBinding()

        binding.loginWebView.run {
            webViewClient = LoginWebViewClient()

            // webview 사용 로그인을 위해 우회
            settings.apply {
                userAgentString = "Mozilla/5.0 AppleWebKit/535.19 Chrome/56.0.0 Mobile Safari/535.19"
                javaScriptEnabled = true
            }

            loadUrl(intent.getStringExtra("url").toString())
        }

    }

    private fun setBinding() {
        binding = ActivityLoginWebviewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    inner class LoginWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (Uri.parse(url).host == "gitudy.com") {
                prefs = SharedPreferences(applicationContext)
                prefs.savePref(SharedPreferencesKey.LOGIN_REDIRECT_URL, url!!)
                Log.d("RU", "${prefs.loadPref(SharedPreferencesKey.LOGIN_REDIRECT_URL, "no url")}")

                startActivity(Intent(view!!.context, InputIdActivity::class.java))
            }
            return false
        }

//        override fun onPageFinished(view: WebView?, url: String?) {
//            super.onPageFinished(view, url)
//            if (Uri.parse(url).host == "gitudy.com") {
//
//            }
//        }
    }
}
