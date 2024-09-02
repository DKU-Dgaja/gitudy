package com.takseha.presentation.ui.feed

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.takseha.presentation.R
import com.takseha.presentation.ui.common.SnackBarHelper
import com.takseha.presentation.viewmodel.common.BaseViewModel
import com.takseha.presentation.viewmodel.feed.StudyApplyViewModel
import com.takseha.presentation.viewmodel.home.MainHomeAlertViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StudyApplyActivity : AppCompatActivity() {
    private val studyApplyViewModel: StudyApplyViewModel by viewModels()
    private lateinit var snackBarHelper: SnackBarHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_apply)

        snackBarHelper = SnackBarHelper(this)
        lifecycleScope.launch {
            studyApplyViewModel.snackbarMessage.collectLatest { message ->
                message?.let {
                    if (it.isNotBlank()) {
                        snackBarHelper.makeSnackBar(findViewById(android.R.id.content), it).show()
                        studyApplyViewModel.resetSnackbarMessage()
                    }
                }
            }
        }
    }
}