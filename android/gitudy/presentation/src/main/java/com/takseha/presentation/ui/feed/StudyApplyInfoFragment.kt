package com.takseha.presentation.ui.feed

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
                // TODO: isAlreadyApplied 필드 확인하고, 이미 신청해놓은 스터디이면 신청 취소버튼 나타나게 처리!
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}