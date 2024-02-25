package com.takseha.presentation.ui.login

import android.content.Intent
import android.net.Uri
import android.net.UrlQuerySanitizer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.takseha.common.model.SharedPreferencesKey
import com.takseha.common.util.SharedPreferences
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityLoginWebviewBinding
import com.takseha.presentation.viewmodel.GetTokenViewModel

class LoginWebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginWebviewBinding
    private lateinit var viewModel: GetTokenViewModel
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_webview)
        setBinding()

        viewModel = ViewModelProvider(this)[GetTokenViewModel::class.java]
        prefs = SharedPreferences(applicationContext)

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

    private fun saveAllToken(url: String?) {
        val sanitizer = UrlQuerySanitizer()
        sanitizer.allowUnregisteredParamaters = true;
        sanitizer.parseUrl(url)

        val platformType = intent.getStringExtra("platformType").toString().uppercase()
        val code = sanitizer.getValue("code")
        val state = sanitizer.getValue("state")

        Log.d("saveAllToken", "code: $code\nstate: $state")

        viewModel.getAllTokens(platformType, code, state)

        viewModel.accessToken.observe(this, Observer {
            prefs.savePref(
                SharedPreferencesKey.ACCESS_TOKEN,
                it
            )
        })

        viewModel.refreshToken.observe(this, Observer {
            prefs.savePref(
                SharedPreferencesKey.REFRESH_TOKEN,
                it
            )
        })
    }

    inner class LoginWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (Uri.parse(url).host == "gitudy.com") {
                saveAllToken(url)

                Log.d("shouldOverrideUrlLoading", "shared pref 저장된 access token: ${prefs.loadPref(SharedPreferencesKey.ACCESS_TOKEN, "0")}\nshared pref 저장된 refresh token: ${prefs.loadPref(SharedPreferencesKey.ACCESS_TOKEN, "0")}")

                startActivity(Intent(view!!.context, SocialLoginCompleteActivity::class.java))
                return true
            }
            return false
        }
    }
}
