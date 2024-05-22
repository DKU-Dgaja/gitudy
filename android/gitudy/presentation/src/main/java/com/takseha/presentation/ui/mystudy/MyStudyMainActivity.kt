package com.takseha.presentation.ui.mystudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityMyStudyMainBinding

class MyStudyMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyStudyMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_study_main)
        setBinding()
        // todo: statusBar 색 변경

        val studyInfoId = intent.getIntExtra("studyInfoId", 0)
        Log.d("MyStudyMainActivity", studyInfoId.toString())

        with(binding) {

        }
    }

    private fun setBinding() {
        binding = ActivityMyStudyMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}