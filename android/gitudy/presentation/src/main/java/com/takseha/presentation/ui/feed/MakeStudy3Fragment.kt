package com.takseha.presentation.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.feed.MakeStudyRequest
import com.takseha.data.dto.feed.StudyPeriodStatus
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.presentation.R
import com.takseha.presentation.adapter.CategoryInStudyRVAdapter
import com.takseha.presentation.databinding.FragmentMakeStudy3Binding
import com.takseha.presentation.viewmodel.feed.MakeStudyViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MakeStudy3Fragment : Fragment() {
    private var _binding: FragmentMakeStudy3Binding? = null
    private val binding get() = _binding!!
    private val viewModel: MakeStudyViewModel by activityViewModels()
    private lateinit var categories: ArrayList<String>
    private var studyCnt: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categories = arguments?.getStringArrayList("categories") ?: arrayListOf()
        studyCnt = requireActivity().intent.getIntExtra("studyCnt", 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMakeStudy3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewNewStudyDetail()
        with(binding) {
            setStudyImg(studyCnt)
            exitBtn.setOnClickListener {
                requireActivity().finish()
            }
            makeStudyBtn.setOnClickListener {
                with(binding) {
                    loadingIndicator.visibility = VISIBLE
                    exitBtn.isEnabled = false
                    makeStudyBtn.isEnabled = false
                }
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.makeNewStudy()
                    viewModel.responseState.collectLatest {
                        if (it != null) {
                            with(binding) {
                                loadingIndicator.visibility = GONE
                                exitBtn.isEnabled = true
                            }
                            if (it) {
                                findNavController()
                                    .navigate(R.id.action_makeStudy3Fragment_to_newStudyFragment)
                            }
                            // TODO: todo 생성 실패 시 로직 구현!
                        }
                    }
                }
            }
        }
    }

    private fun viewNewStudyDetail() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.newStudyInfoState.collectLatest {
                setNewStudyDetail(it)
            }
        }
    }

    private fun setStudyImg(studyCnt: Int) {
        with(binding) {
            when (studyCnt % 10) {
                0 -> studyImg.setImageResource(R.drawable.bg_mystudy_small_10)
                1 -> studyImg.setImageResource(R.drawable.bg_mystudy_small_9)
                2 -> studyImg.setImageResource(R.drawable.bg_mystudy_small_8)
                3 -> studyImg.setImageResource(R.drawable.bg_mystudy_small_7)
                4 -> studyImg.setImageResource(R.drawable.bg_mystudy_small_6)
                5 -> studyImg.setImageResource(R.drawable.bg_mystudy_small_5)
                6 -> studyImg.setImageResource(R.drawable.bg_mystudy_small_4)
                7 -> studyImg.setImageResource(R.drawable.bg_mystudy_small_3)
                8 -> studyImg.setImageResource(R.drawable.bg_mystudy_small_2)
                else -> studyImg.setImageResource(R.drawable.bg_mystudy_small_1)
            }
        }
    }

    private fun setNewStudyDetail(
        studyInfo: MakeStudyRequest
    ) {
        with(binding) {
            studyName.text = studyInfo.topic
            studyDetail.text = studyInfo.info
            isPublicStudy.text =
                if (studyInfo.status == StudyStatus.STUDY_PUBLIC) getString(R.string.study_unlock) else getString(
                    R.string.study_lock
                )
            maxMemberText.text = getString(R.string.feed_member_full_number, studyInfo.maximumMember)
            githubRepoLink.text = studyInfo.repositoryName
            commitRule.text =
                when (studyInfo.periodType) {
                    StudyPeriodStatus.STUDY_PERIOD_WEEK -> getString(R.string.feed_rule_week)
                    StudyPeriodStatus.STUDY_PERIOD_EVERYDAY -> getString(
                        R.string.feed_rule_everyday
                    )
                    else -> getString(R.string.feed_rule_free)
                }
            setCategoryList(categories)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}