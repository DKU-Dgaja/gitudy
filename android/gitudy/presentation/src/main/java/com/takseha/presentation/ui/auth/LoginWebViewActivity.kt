package com.takseha.presentation.ui.auth

import android.content.Intent
import android.net.Uri
import android.net.UrlQuerySanitizer
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.takseha.data.dto.auth.login.LoginRequest
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityLoginWebviewBinding
import com.takseha.presentation.ui.common.SnackBarHelper
import com.takseha.presentation.viewmodel.auth.LoginWebViewViewModel
import com.takseha.presentation.viewmodel.common.BaseApplicationViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginWebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginWebviewBinding
    private val viewModel: LoginWebViewViewModel by viewModels()
    private lateinit var snackBarHelper: SnackBarHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_webview)
        window.statusBarColor = ContextCompat.getColor(this, R.color.WHITE)
        setBinding()

        snackBarHelper = SnackBarHelper(this)
        lifecycleScope.launch {
            (viewModel as? BaseApplicationViewModel)?.snackbarMessage?.collectLatest { message ->
                message?.let {
                    if (it.isNotBlank()) {
                        snackBarHelper.makeSnackBar(findViewById(android.R.id.content), it).show()
                        (viewModel as? BaseApplicationViewModel)?.resetSnackbarMessage()
                    }
                }
            }
        }

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

    private fun getAuthCode(url: String?): LoginRequest {
        val sanitizer = UrlQuerySanitizer()
        sanitizer.allowUnregisteredParamaters = true;
        sanitizer.parseUrl(url)

        return LoginRequest(sanitizer.getValue("code"), sanitizer.getValue("state"))
    }

    inner class LoginWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (Uri.parse(url).host == "gitudy.com") {
                val platformType = intent.getStringExtra("platformType").toString().uppercase()
                val authCode = getAuthCode(url)

                viewModel.saveAllTokens(platformType, authCode.code, authCode.state)
                viewModel.role.observe(this@LoginWebViewActivity) {
                    val intent = Intent(view!!.context, SocialLoginCompleteActivity::class.java)
                    intent.putExtra("role", it.toString())
                    startActivity(intent)
                }
                return true
            }
            return false
        }
    }
}
