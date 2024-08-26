package com.takseha.presentation.ui.mystudy

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.takseha.presentation.R
import com.takseha.presentation.viewmodel.mystudy.MyStudyMainViewModel
import kotlinx.coroutines.launch

class MyStudyMainActivity : AppCompatActivity() {
    private val viewModel: MyStudyMainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_study_main)

        val studyInfoId = intent.getIntExtra("studyInfoId", 0)

        lifecycleScope.launch {
            viewModel.apply {
                getMyStudyInfo(studyInfoId)
                getStudyComments(studyInfoId, 3)
            }
        }
    }
}