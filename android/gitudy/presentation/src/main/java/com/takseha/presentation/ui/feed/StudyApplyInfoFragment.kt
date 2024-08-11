package com.takseha.presentation.ui.feed

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.feed.StudyPeriodStatus
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.data.dto.mystudy.StudyInfoResponse
import com.takseha.presentation.R
import com.takseha.presentation.adapter.CategoryInStudyRVAdapter
import com.takseha.presentation.databinding.FragmentStudyApplyInfoBinding
import com.takseha.presentation.ui.common.CustomDialog
import com.takseha.presentation.viewmodel.feed.StudyApplyViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs

class StudyApplyInfoFragment : Fragment() {
    private var _binding: FragmentStudyApplyInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudyApplyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudyApplyInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val studyInfoId = activity?.intent?.getIntExtra("studyInfoId", 0) ?: 0
        val studyImgColor =
            if (activity?.intent?.getStringExtra("studyImgColor") == "" || activity?.intent?.getStringExtra("studyImgColor") == "string") "#000000" else activity?.intent?.getStringExtra("studyImgColor")

        requireActivity().window.statusBarColor = Color.parseColor(studyImgColor)

        viewModel.getStudyInfo(studyInfoId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                setStudyInfo(studyInfoId, studyImgColor!!, it.studyInfo)
            }
        }

        with(binding) {
            backBtn.setOnClickListener {
                requireActivity().finish()
            }
            studyEnterBtn.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_studyApplyInfoFragment_to_studyApplyMessageFragment)
            }
            applyCancelBtn.setOnClickListener {
                showWithdrawApplyStudyDialog(studyInfoId)
            }
            // TODO: 스터디 공유 기능 추후 구현
//            studyLinkCopyBtn.setOnClickListener {
//            }
        }
    }

    private fun setStudyInfo(studyInfoId: Int, studyImgColor: String, studyInfo: StudyInfoResponse) {
        with(binding) {
            studyBackgroundImg.setBackgroundColor(Color.parseColor(studyImgColor))
            if (studyInfo.isWaiting) {
                applyCancelBtn.visibility = VISIBLE
                studyEnterBtn.visibility = GONE
            } else {
                applyCancelBtn.visibility = GONE
                studyEnterBtn.visibility = VISIBLE
            }
            studyName.text = studyInfo.topic
            studyDetail.text = studyInfo.info
            studyRuleText.text = setCommitRule(studyInfo.periodType)
            isStudyOpenText.text = setStudyStatus(studyInfo.status)
            studyRankText.text = getString(
                R.string.study_team_rank, studyInfo.score,
                studyInfoId - 15
            )
            teamRankFullText.text = getString(
                R.string.study_team_rank_full,
                if (studyInfoId - 10 > 0) studyInfoId - 10 else abs(studyInfoId - 10) + 2,
                if (studyInfo.lastCommitDay == null) "없음" else studyInfo.lastCommitDay
            )
            studyGithubLinkText.text = studyInfo.githubLinkInfo.branchName
            studyMemberCntText.text = String.format(
                getString(R.string.feed_member_number),
                studyInfo.currentMember,
                studyInfo.maximumMember
            )
            setCategoryList(studyInfo.categoryNames)
        }
    }

    private fun setCommitRule(periodType: StudyPeriodStatus): String {
        when (periodType) {
            StudyPeriodStatus.STUDY_PERIOD_EVERYDAY -> return requireContext().getString(R.string.feed_rule_everyday)
            StudyPeriodStatus.STUDY_PERIOD_WEEK -> return requireContext().getString(R.string.feed_rule_week)
            StudyPeriodStatus.STUDY_PERIOD_NONE -> return requireContext().getString(R.string.feed_rule_free)
        }
    }

    private fun setStudyStatus(status: StudyStatus): String {
        when (status) {
            StudyStatus.STUDY_PRIVATE -> return requireContext().getString(R.string.study_lock)
            StudyStatus.STUDY_PUBLIC -> return requireContext().getString(R.string.study_unlock)
            StudyStatus.STUDY_DELETED -> return requireContext().getString(R.string.study_deleted)
        }
    }

    private fun setCategoryList(categoryList: List<String>) {
        with(binding) {
            val categoryInStudyRVAdapter = CategoryInStudyRVAdapter(requireContext(), categoryList)
            tagList.adapter = categoryInStudyRVAdapter
            tagList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun showWithdrawApplyStudyDialog(studyInfoId: Int) {
        val customDialog = CustomDialog(requireContext())
        customDialog.setAlertText(getString(R.string.feed_apply_study_cancel))
        customDialog.setOnConfirmClickListener {
            viewModel.withdrawApplyStudy(studyInfoId)
            binding.applyCancelBtn.visibility = GONE
            binding.studyEnterBtn.visibility = VISIBLE
        }
        customDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}