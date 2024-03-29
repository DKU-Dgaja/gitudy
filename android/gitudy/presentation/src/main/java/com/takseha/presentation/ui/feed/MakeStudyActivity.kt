package com.takseha.presentation.ui.feed

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityMakeStudyBinding

class MakeStudyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_study)
        window.statusBarColor = ContextCompat.getColor(this, R.color.WHITE)
    }
}