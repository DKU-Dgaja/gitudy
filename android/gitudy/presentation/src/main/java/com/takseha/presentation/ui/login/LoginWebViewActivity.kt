package com.takseha.presentation.ui.login

import android.content.Intent
import android.net.Uri
import android.net.UrlQuerySanitizer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import com.takseha.common.util.SharedPreferences
import com.takseha.data.dto.AuthCodeRequest
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityLoginWebviewBinding
import com.takseha.presentation.viewmodel.GetTokenViewModel

class LoginWebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginWebviewBinding
    private lateinit var viewModel: GetTokenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_webview)
        setBinding()

        viewModel = ViewModelProvider(this)[GetTokenViewModel::class.java]

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

    private fun getAuthCode(url: String?): AuthCodeRequest {
        val sanitizer = UrlQuerySanitizer()
        sanitizer.allowUnregisteredParamaters = true;
        sanitizer.parseUrl(url)

        return AuthCodeRequest(sanitizer.getValue("code"), sanitizer.getValue("state"))
    }

    inner class LoginWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (Uri.parse(url).host == "gitudy.com") {
                val platformType = intent.getStringExtra("platformType").toString().uppercase()
                val authCode = getAuthCode(url)

                viewModel.saveAllTokens(platformType, authCode.code, authCode.state)

                Log.d("saveAllToken", "code: ${authCode.code}\nstate: ${authCode.state}")

                startActivity(Intent(view!!.context, SocialLoginCompleteActivity::class.java))
                return true
            }
            return false
        }
    }
}
