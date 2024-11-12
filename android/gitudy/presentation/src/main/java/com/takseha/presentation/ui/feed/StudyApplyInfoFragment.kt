package com.takseha.presentation.ui.feed

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.feed.StudyPeriodStatus
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.presentation.R
import com.takseha.presentation.adapter.CategoryInStudyRVAdapter
import com.takseha.presentation.databinding.FragmentStudyApplyInfoBinding
import com.takseha.presentation.ui.common.CustomSetDialog
import com.takseha.presentation.viewmodel.feed.StudyApplyViewModel
import com.takseha.presentation.viewmodel.feed.StudyMainInfoState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StudyApplyInfoFragment : Fragment() {
    private var _binding: FragmentStudyApplyInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudyApplyViewModel by activityViewModels()
    private var studyInfoId: Int = 0
    private var studyImgColor: String = "0"
    private var studyStatus: StudyStatus? = null
    private val colorList = listOf(
        R.color.BG_10,
        R.color.BG_9,
        R.color.BG_8,
        R.color.BG_7,
        R.color.BG_6,
        R.color.BG_5,
        R.color.BG_4,
        R.color.BG_3,
        R.color.BG_2,
        R.color.BG_1
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studyInfoId = requireActivity().intent.getIntExtra("studyInfoId", 0)
        studyImgColor = requireActivity().intent.getStringExtra("studyImgColor") ?: "0"
        studyStatus = requireActivity().intent.getSerializableExtra("studyStatus") as StudyStatus
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                if (studyStatus != StudyStatus.STUDY_INACTIVE) {
                    requireActivity().window.statusBarColor = ContextCompat.getColor(
                        requireContext(),
                        colorList[it.studyInfo.profileImageUrl.toIntOrNull() ?: 0]
                    )
                } else {
                    requireActivity().window.statusBarColor = ContextCompat.getColor(
                        requireContext(),
                        R.color.GS_300
                    )
                }
                setStudyInfo(it.studyInfo.profileImageUrl, it)
            }
        }

        with(binding) {
            studyApplySwipeRefreshLayout.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    launch { viewModel.getStudyInfo(studyInfoId) }
                    launch { viewModel.getStudyRank(studyInfoId) }
                    launch { viewModel.checkBookmarkStatus(studyInfoId) }
                    studyApplySwipeRefreshLayout.isRefreshing = false
                }
            }
            backBtn.setOnClickListener {
                requireActivity().finish()
            }
            copyBtn.setOnClickListener {
                val textToCopy = studyGithubLinkText.text
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("label", textToCopy)
                clipboard.setPrimaryClip(clip)
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
                    viewModel.setBookmarkStatus(studyInfoId)
                }
            }
            // TODO: 스터디 공유 기능 추후 구현
//            studyLinkCopyBtn.setOnClickListener {
//            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            launch { viewModel.getStudyInfo(studyInfoId) }
            launch { viewModel.getStudyRank(studyInfoId) }
            launch { viewModel.checkBookmarkStatus(studyInfoId) }
        }
    }

    private fun setStudyInfo(
        studyImgColor: String,
        studyMainInfoState: StudyMainInfoState
    ) {
        val studyInfo = studyMainInfoState.studyInfo

        with(binding) {
            if (studyStatus != StudyStatus.STUDY_INACTIVE) {
                val studyImgSrc = setStudyImg(studyImgColor.toIntOrNull() ?: 0)
                studyImg.setImageResource(studyImgSrc)
                studyEndTag.visibility = GONE
                if (studyInfo.isLeader) {
                    studyEnterBtn.isEnabled = false
                    studyEnterBtn.text = "이미 가입한 스터디입니다"
                } else if(studyInfo.currentMember == studyInfo.maximumMember) {
                    studyEnterBtn.isEnabled = false
                    studyEnterBtn.text = "모집 완료"
                } else {
                    studyEnterBtn.isEnabled = true
                    studyEnterBtn.text = "스터디 신청하기"
                }
                if (studyInfo.isWaiting) {
                    applyCancelBtn.visibility = VISIBLE
                    studyEnterBtn.visibility = GONE
                } else {
                    applyCancelBtn.visibility = GONE
                    studyEnterBtn.visibility = VISIBLE
                }
            } else {
                studyImg.setImageResource(R.drawable.bg_mystudy_full_default)
                studyEndTag.visibility = VISIBLE
                studyEnterBtn.isEnabled = false
                studyEnterBtn.text = "활동 종료"
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
                studyInfo.lastCommitDay ?: "없음"
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
            StudyStatus.STUDY_PRIVATE -> getString(R.string.study_lock)
            StudyStatus.STUDY_PUBLIC -> getString(R.string.study_unlock)
            StudyStatus.STUDY_DELETED -> getString(R.string.study_deleted)
            else -> getString(R.string.study_inactive)
        }
    }

    private fun setStudyImg(currentIdx: Int): Int {
        return when (currentIdx) {
            0 -> R.drawable.bg_mystudy_full_10
            1 -> R.drawable.bg_mystudy_full_9
            2 -> R.drawable.bg_mystudy_full_8
            3 -> R.drawable.bg_mystudy_full_7
            4 -> R.drawable.bg_mystudy_full_6
            5 -> R.drawable.bg_mystudy_full_5
            6 -> R.drawable.bg_mystudy_full_4
            7 -> R.drawable.bg_mystudy_full_3
            8 -> R.drawable.bg_mystudy_full_2
            else -> R.drawable.bg_mystudy_full_1
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