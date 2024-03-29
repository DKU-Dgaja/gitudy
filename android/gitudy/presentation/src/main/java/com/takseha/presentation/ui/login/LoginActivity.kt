package com.takseha.presentation.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        setBinding()

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.githubLoginBtn.setOnClickListener {

        }
        binding.otherLoginBtn.setOnClickListener {
            startActivity(Intent(this, SubLoginActivity::class.java))
        }

    }
    private fun setBinding() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}