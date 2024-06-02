package com.takseha.presentation.ui.mystudy

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.takseha.data.dto.feed.StudyPeriod
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityMyStudyMainBinding
import com.takseha.presentation.viewmodel.mystudy.MyStudyMainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs

class MyStudyMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyStudyMainBinding
    private val viewModel: MyStudyMainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_study_main)
        setBinding()
        // Todo: statusBar 색 변경

        val studyInfoId = intent.getIntExtra("studyInfoId", 0)
        val studyImgColor = intent.getStringExtra("studyImgColor")
        Log.d("MyStudyMainActivity", studyInfoId.toString())

        viewModel.getMyStudyInfo(studyInfoId)
        setMyStudyInfo(studyInfoId, studyImgColor!!)

        with(binding) {
            backBtn.setOnClickListener {
                finish()
            }
            todoAdditionBtn.setOnClickListener {
                startActivity(Intent(baseContext, AddTodoActivity::class.java))
            }
        }
    }

    private fun setMyStudyInfo(studyInfoId: Int, studyImgColor: String) {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                with(binding) {
                    studyBackgroundImg.setBackgroundColor(Color.parseColor(studyImgColor))
                    studyName.text = it.myStudyInfo.topic
                    studyRule.text = setCommitRule(it.myStudyInfo.periodType)
                    studyInfo.text = it.myStudyInfo.info
                    isStudyOpenText.text = setStudyStatus(it.myStudyInfo.status)
                    studyRankText.text = String.format(
                        getString(R.string.study_team_rank),
                        studyInfoId * 10 + 2,
                        if (studyInfoId - 10 > 0) studyInfoId - 10 else abs(studyInfoId - 10) + 2
                    )
                    studyGithubLinkText.text = it.myStudyInfo.githubLinkInfo.branchName
                }
            }
        }
    }

    private fun setCommitRule(periodType: StudyPeriod): String {
        when (periodType) {
            StudyPeriod.STUDY_PERIOD_EVERYDAY -> return baseContext.getString(R.string.feed_rule_everyday)
            StudyPeriod.STUDY_PERIOD_WEEK -> return baseContext.getString(R.string.feed_rule_week)
            StudyPeriod.STUDY_PERIOD_NONE -> return baseContext.getString(R.string.feed_rule_free)
        }
    }

    private fun setStudyStatus(status: StudyStatus): String {
        when (status) {
            StudyStatus.STUDY_PRIVATE -> return baseContext.getString(R.string.study_lock)
            StudyStatus.STUDY_PUBLIC -> return baseContext.getString(R.string.study_unlock)
            StudyStatus.STUDY_DELETED -> return baseContext.getString(R.string.study_deleted)
        }
    }

    private fun setBinding() {
        binding = ActivityMyStudyMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}