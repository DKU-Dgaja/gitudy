package com.takseha.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.takseha.data.dto.auth.login.RoleStatus
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityAdminLoginBinding
import com.takseha.presentation.databinding.ActivityLoginBinding
import com.takseha.presentation.databinding.LayoutSnackbarGreyBinding
import com.takseha.presentation.databinding.LayoutSnackbarRedBinding
import com.takseha.presentation.firebase.MyFirebaseMessagingService
import com.takseha.presentation.ui.common.KeyboardUtils
import com.takseha.presentation.ui.common.SnackBarHelper
import com.takseha.presentation.viewmodel.auth.AdminLoginViewModel
import com.takseha.presentation.viewmodel.auth.LoginViewModel
import com.takseha.presentation.viewmodel.common.BaseApplicationViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AdminLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminLoginBinding
    private val viewModel: AdminLoginViewModel by viewModels()
    private lateinit var snackBarHelper: SnackBarHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_login)
        window.statusBarColor = ContextCompat.getColor(this, R.color.BACKGROUND)
        setBinding()
        setupUI(binding.root)

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
            adminLoginBtn.setOnClickListener {
                val id = idEditText.text.toString()
                val password = pwEditText.text.toString()
                if (id == "tester" && password == "1234") {
                    wrongInfoAlert.visibility = GONE
                    lifecycleScope.launch {
                        viewModel.getAdminTokens(id, password)
                        viewModel.loginState.collectLatest {
                            if (it == true) {
                                val intent = Intent(this@AdminLoginActivity, SocialLoginCompleteActivity::class.java)
                                intent.putExtra("role", RoleStatus.ADMIN.toString())
                                startActivity(intent)
                            }
                        }
                    }
                } else {
                    wrongInfoAlert.visibility = VISIBLE
                }
            }

            backBtn.setOnClickListener {
                finish()
            }
        }
    }

    private fun setupUI(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    this.let { KeyboardUtils.hideKeyboard(it) }
                }
                false
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    private fun setBinding() {
        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}