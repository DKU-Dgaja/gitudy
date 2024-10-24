package com.takseha.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityLoginBinding
import com.takseha.presentation.ui.common.SnackBarHelper
import com.takseha.presentation.viewmodel.auth.LoginViewModel
import com.takseha.presentation.viewmodel.common.BaseApplicationViewModel
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var snackBarHelper: SnackBarHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.statusBarColor = ContextCompat.getColor(this, R.color.BACKGROUND)
        setBinding()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        })

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

        with(binding) {
            githubLoginBtn.setOnClickListener {
                startLogin("GITHUB")
            }
            adminLoginBtn.setOnClickListener {
                startActivity(Intent(baseContext, AdminLoginActivity::class.java))
            }
//            otherLoginBtn.setOnClickListener {
//                startActivity(Intent(baseContext, SubLoginActivity::class.java))
//            }
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