package com.takseha.presentation.ui.feed

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.feed.StudyPeriodStatus
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.data.dto.mystudy.StudyInfoResponse
import com.takseha.presentation.R
import com.takseha.presentation.adapter.CategoryInStudyRVAdapter
import com.takseha.presentation.databinding.ActivityStudyApplyBinding
import com.takseha.presentation.ui.common.CustomDialog
import com.takseha.presentation.viewmodel.feed.StudyApplyViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs


class StudyApplyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudyApplyBinding
    private val viewModel: StudyApplyViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_apply)
        setBinding()

        val studyInfoId = intent.getIntExtra("studyInfoId", 0)
        val studyImgColor =
            if (intent.getStringExtra("studyImgColor") == "" || intent.getStringExtra("studyImgColor") == "string") "#000000" else intent.getStringExtra("studyImgColor")

        window.statusBarColor = Color.parseColor(studyImgColor)

        viewModel.getStudyInfo(studyInfoId)

        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                setStudyInfo(studyInfoId, studyImgColor!!, it.studyInfo)
            }
        }

        with(binding) {
            backBtn.setOnClickListener {
                finish()
            }
            studyEnterBtn.setOnClickListener {
                showApplyStudyDialog(studyInfoId, "", "가입해서 열심히 활동할게요~")
            }
            // TODO: 스터디 공유 기능 추후 구현
//            studyLinkCopyBtn.setOnClickListener {
//            }
        }
    }

    private fun setStudyInfo(studyInfoId: Int, studyImgColor: String, myStudyInfo: StudyInfoResponse) {
        with(binding) {
            studyBackgroundImg.setBackgroundColor(Color.parseColor(studyImgColor))
            studyName.text = myStudyInfo.topic
            studyDetail.text = myStudyInfo.info
            studyRuleText.text = setCommitRule(myStudyInfo.periodType)
            isStudyOpenText.text = setStudyStatus(myStudyInfo.status)
            studyRankText.text = getString(
                R.string.study_team_rank, 300 - studyInfoId * 10,
                studyInfoId - 15
            )
            teamRankFullText.text = getString(
                R.string.study_team_rank_full,
                if (studyInfoId - 10 > 0) studyInfoId - 10 else abs(studyInfoId - 10) + 2,
                if (myStudyInfo.lastCommitDay == null) "없음" else myStudyInfo.lastCommitDay
            )
            studyGithubLinkText.text = myStudyInfo.githubLinkInfo.branchName
            studyMemberCntText.text = String.format(
                getString(R.string.feed_member_number),
                myStudyInfo.currentMember,
                myStudyInfo.maximumMember
            )
            setCategoryList(myStudyInfo.categoryNames)
        }
    }

    private fun setCommitRule(periodType: StudyPeriodStatus): String {
        when (periodType) {
            StudyPeriodStatus.STUDY_PERIOD_EVERYDAY -> return baseContext.getString(R.string.feed_rule_everyday)
            StudyPeriodStatus.STUDY_PERIOD_WEEK -> return baseContext.getString(R.string.feed_rule_week)
            StudyPeriodStatus.STUDY_PERIOD_NONE -> return baseContext.getString(R.string.feed_rule_free)
        }
    }

    private fun setStudyStatus(status: StudyStatus): String {
        when (status) {
            StudyStatus.STUDY_PRIVATE -> return baseContext.getString(R.string.study_lock)
            StudyStatus.STUDY_PUBLIC -> return baseContext.getString(R.string.study_unlock)
            StudyStatus.STUDY_DELETED -> return baseContext.getString(R.string.study_deleted)
        }
    }

    private fun setCategoryList(categoryList: List<String>) {
        with(binding) {
            val categoryInStudyRVAdapter = CategoryInStudyRVAdapter(this@StudyApplyActivity, categoryList)
            tagList.adapter = categoryInStudyRVAdapter
            tagList.layoutManager = LinearLayoutManager(this@StudyApplyActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun showApplyStudyDialog(studyInfoId: Int, joinCode: String, message: String) {
        val customDialog = CustomDialog(this)
        customDialog.setAlertText(getString(R.string.feed_apply_study))
        customDialog.setOnConfirmClickListener {
            viewModel.applyStudy(studyInfoId, joinCode, message)

            // todo: 신청 취소 관련 로직 구현
            binding.studyEnterBtn.apply {
                text = "신청 취소하기"
                setTextColor(Color.parseColor("#ffffff"))
                backgroundTintList = ColorStateList.valueOf(Color.parseColor("#000000"))
            }
        }
        customDialog.show()
    }

    private fun setBinding() {
        binding = ActivityStudyApplyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}