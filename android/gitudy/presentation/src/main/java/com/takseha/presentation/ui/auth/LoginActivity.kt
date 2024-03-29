package com.takseha.presentation.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityLoginBinding
import com.takseha.presentation.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.statusBarColor = ContextCompat.getColor(this, R.color.BACKGROUND)
        setBinding()

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        with(binding) {
            githubLoginBtn.setOnClickListener {
                startLogin("GITHUB")
            }
            otherLoginBtn.setOnClickListener {
                startActivity(Intent(baseContext, SubLoginActivity::class.java))
            }
        }
    }
    private fun setBinding() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun startLogin(platformType: String) {
        viewModel.startLogin(platformType)
        viewModel.loginPageUrl.observe(this) {
            val intent = Intent(this, LoginWebViewActivity::class.java)
            intent.putExtra("url", it)
            intent.putExtra("platformType", platformType)
            startActivity(intent)
        }
    }
}