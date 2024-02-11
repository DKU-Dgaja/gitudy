package com.takseha.presentation.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityLoginBinding
import com.takseha.presentation.ui.mystudy.CommentActivity
import com.takseha.presentation.ui.mystudy.MyStudyMainActivity
import com.takseha.presentation.ui.profile.ProfileEditActivity
import com.takseha.presentation.ui.profile.ProfileHomeFragment

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.githubLoginBtn.setOnClickListener {

        }
        binding.otherLoginBtn.setOnClickListener {
            startActivity(Intent(this, SubLoginActivity::class.java))
        }

    }
}