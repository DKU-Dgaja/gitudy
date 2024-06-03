package com.takseha.presentation.ui.mystudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityCommitConventionBinding

class CommitConventionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommitConventionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commit_convention)
    }
}