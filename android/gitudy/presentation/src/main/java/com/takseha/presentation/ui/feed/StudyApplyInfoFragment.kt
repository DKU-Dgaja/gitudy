package com.takseha.presentation.ui.feed

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import com.takseha.presentation.ui.common.CustomSetDialog
import com.takseha.presentation.viewmodel.feed.StudyApplyViewModel
import com.takseha.presentation.viewmodel.feed.StudyInfoWithBookmarkStatus
import com.takseha.presentation.viewmodel.feed.StudyMainInfoState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs

class StudyApplyInfoFragment : Fragment() {
    private var _binding: FragmentStudyApplyInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudyApplyViewModel by viewModels()
    private val colorList = listOf(
        R.color.BG_1,
        R.color.BG_2,
        R.color.BG_3,
        R.color.BG_4,
        R.color.BG_5,
        R.color.BG_6,
        R.color.BG_7,
        R.color.BG_8,
        R.color.BG_9,
        R.color.BG_10
    )

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
        val studyInfoId = requireActivity().intent.getIntExtra("studyInfoId", 0) ?: 0
        val studyImgColor = requireActivity().intent.getStringExtra("studyImgColor") ?: "0"

        requireActivity().window.statusBarColor = ContextCompat.getColor(
            requireContext(),
            colorList[studyImgColor!!.toInt()]
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getStudyInfo(studyInfoId)
            viewModel.uiState.collectLatest {
                setStudyInfo(studyInfoId, studyImgColor, it)
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
            bookmarkBtn.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.apply {
                        setBookmarkStatus(studyInfoId)
                        getStudyInfo(studyInfoId)
                    }
                }
            }
            // TODO: 스터디 공유 기능 추후 구현
//            studyLinkCopyBtn.setOnClickListener {
//            }
        }
    }

    private fun setStudyInfo(
        studyInfoId: Int,
        studyImgColor: String,
        studyMainInfoState: StudyMainInfoState
    ) {
        val studyInfo = studyMainInfoState.studyInfo

        with(binding) {
            val studyImgSrc = setStudyImg(studyImgColor.toInt())
            studyImg.setImageResource(studyImgSrc)
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
                R.string.study_team_rank, studyInfo.score, studyMainInfoState.rank
            )
            teamRankFullText.text = getString(
                R.string.study_team_rank_full,
                studyMainInfoState.rank,
                studyInfo.lastCommitDay
            )
            studyGithubLinkText.text = getString(
                R.string.study_github_link,
                studyInfo.githubLinkInfo.owner,
                studyInfo.githubLinkInfo.name
            )
            studyMemberCntText.text = String.format(
                getString(R.string.feed_member_number),
                studyInfo.currentMember,
                studyInfo.maximumMember
            )
            setCategoryList(studyInfo.categoryNames)
            Log.d("StudyApplyInfoFragment", studyMainInfoState.isMyBookmark.toString())
            val drawable = if (studyMainInfoState.isMyBookmark == true) {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_feed_save_green)
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_feed_save)
            }
            bookmarkBtn.setImageDrawable(drawable)
        }
    }

    private fun setCommitRule(periodType: StudyPeriodStatus): String {
        return when (periodType) {
            StudyPeriodStatus.STUDY_PERIOD_EVERYDAY -> requireContext().getString(R.string.feed_rule_everyday)
            StudyPeriodStatus.STUDY_PERIOD_WEEK -> requireContext().getString(R.string.feed_rule_week)
            StudyPeriodStatus.STUDY_PERIOD_NONE -> requireContext().getString(R.string.feed_rule_free)
        }
    }

    private fun setStudyStatus(status: StudyStatus): String {
        return when (status) {
            StudyStatus.STUDY_PRIVATE -> requireContext().getString(R.string.study_lock)
            StudyStatus.STUDY_PUBLIC -> requireContext().getString(R.string.study_unlock)
            StudyStatus.STUDY_DELETED -> requireContext().getString(R.string.study_deleted)
        }
    }

    private fun setStudyImg(currentIdx: Int): Int {
        return when (currentIdx) {
            0 -> R.drawable.bg_mystudy_full_1
            1 -> R.drawable.bg_mystudy_full_2
            2 -> R.drawable.bg_mystudy_full_3
            3 -> R.drawable.bg_mystudy_full_4
            4 -> R.drawable.bg_mystudy_full_5
            5 -> R.drawable.bg_mystudy_full_6
            6 -> R.drawable.bg_mystudy_full_7
            7 -> R.drawable.bg_mystudy_full_8
            8 -> R.drawable.bg_mystudy_full_9
            else -> R.drawable.bg_mystudy_full_10
        }
    }

    private fun setCategoryList(categoryList: List<String>) {
        with(binding) {
            val categoryInStudyRVAdapter = CategoryInStudyRVAdapter(requireContext(), categoryList)
            tagList.adapter = categoryInStudyRVAdapter
            tagList.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun showWithdrawApplyStudyDialog(studyInfoId: Int) {
        val customSetDialog = CustomSetDialog(requireContext())
        customSetDialog.setAlertText(getString(R.string.feed_apply_study_cancel))
        customSetDialog.setOnConfirmClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.withdrawApplyStudy(studyInfoId)
                binding.applyCancelBtn.visibility = GONE
                binding.studyEnterBtn.visibility = VISIBLE
            }
        }
        customSetDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}