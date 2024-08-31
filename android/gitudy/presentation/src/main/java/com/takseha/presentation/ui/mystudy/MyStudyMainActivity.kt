package com.takseha.presentation.ui.mystudy

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.takseha.presentation.R
import com.takseha.presentation.ui.common.SnackBarHelper
import com.takseha.presentation.viewmodel.mystudy.AddTodoViewModel
import com.takseha.presentation.viewmodel.mystudy.MyStudyMainViewModel
import com.takseha.presentation.viewmodel.mystudy.MyStudySettingViewModel
import com.takseha.presentation.viewmodel.mystudy.StudyCommentBoardViewModel
import com.takseha.presentation.viewmodel.mystudy.TodoViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyStudyMainActivity : AppCompatActivity() {
    private val myStudyMainViewModel: MyStudyMainViewModel by viewModels()
    private val todoViewModel: TodoViewModel by viewModels()
    private val addTodoViewModel: AddTodoViewModel by viewModels()
    private val studyCommentBoardViewModel: StudyCommentBoardViewModel by viewModels()
    private val myStudySettingViewModel: MyStudySettingViewModel by viewModels()
    private lateinit var snackBarHelper: SnackBarHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_study_main)

        val studyInfoId = intent.getIntExtra("studyInfoId", 0)

        snackBarHelper = SnackBarHelper(this)
        lifecycleScope.launch {
            myStudyMainViewModel.snackbarMessage.collectLatest { message ->
                message?.let {
                    if (it.isNotBlank()) {
                        snackBarHelper.makeSnackBar(findViewById(android.R.id.content), it).show()
                        myStudyMainViewModel.resetSnackbarMessage()
                    }
                }
            }
        }

        lifecycleScope.launch {
            todoViewModel.snackbarMessage.collectLatest { message ->
                message?.let {
                    if (it.isNotBlank()) {
                        snackBarHelper.makeSnackBar(findViewById(android.R.id.content), it).show()
                        todoViewModel.resetSnackbarMessage()
                    }
                }
            }
        }

        lifecycleScope.launch {
            addTodoViewModel.snackbarMessage.collectLatest { message ->
                message?.let {
                    if (it.isNotBlank()) {
                        snackBarHelper.makeSnackBar(findViewById(android.R.id.content), it).show()
                        addTodoViewModel.resetSnackbarMessage()
                    }
                }
            }
        }

        lifecycleScope.launch {
            studyCommentBoardViewModel.snackbarMessage.collectLatest { message ->
                message?.let {
                    if (it.isNotBlank()) {
                        snackBarHelper.makeSnackBar(findViewById(android.R.id.content), it).show()
                        studyCommentBoardViewModel.resetSnackbarMessage()
                    }
                }
            }
        }

        lifecycleScope.launch {
            myStudySettingViewModel.snackbarMessage.collectLatest { message ->
                message?.let {
                    if (it.isNotBlank()) {
                        snackBarHelper.makeSnackBar(findViewById(android.R.id.content), it).show()
                        myStudySettingViewModel.resetSnackbarMessage()
                    }
                }
            }
        }

        lifecycleScope.launch {
            myStudyMainViewModel.apply {
                getMyStudyInfo(studyInfoId)
                getStudyComments(studyInfoId, 3)
            }
        }
    }
}