package com.takseha.presentation.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivitySubLoginBinding

class SubLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_login)

        binding = ActivitySubLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.kakaoLoginBtn.setOnClickListener {

        }
        binding.googleLoginBtn.setOnClickListener {

        }
    }
}