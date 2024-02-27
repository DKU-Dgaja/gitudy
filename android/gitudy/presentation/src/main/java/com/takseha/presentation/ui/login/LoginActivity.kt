package com.takseha.presentation.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityLoginBinding
import com.takseha.presentation.viewmodel.StartLoginViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: StartLoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setBinding()

        viewModel = ViewModelProvider(this)[StartLoginViewModel::class.java]

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