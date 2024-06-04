package com.takseha.presentation.ui.feed

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityStudyApplyBinding

class StudyApplyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudyApplyBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_apply)
        setBinding()

        val studyInfoId = intent.getIntExtra("studyInfoId", 0)
        val studyImgColor = intent.getStringExtra("studyImgColor")

    }

    private fun setBinding() {
        binding = ActivityStudyApplyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}