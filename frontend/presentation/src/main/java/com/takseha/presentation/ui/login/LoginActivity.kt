package com.takseha.presentation.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import com.takseha.presentation.R
import com.takseha.presentation.ui.mystudy.CommentActivity
import com.takseha.presentation.ui.mystudy.MyStudyMainActivity
import com.takseha.presentation.ui.profile.ProfileEditActivity
import com.takseha.presentation.ui.profile.ProfileHomeFragment

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val b = findViewById<LinearLayout>(R.id.githubLoginBtn)
        b.setOnClickListener {
            startActivity(Intent(this, MyStudyMainActivity::class.java))
        }

    }
}