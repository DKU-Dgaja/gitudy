package com.takseha.presentation.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.takseha.data.dto.feed.MakeStudyRequest
import com.takseha.data.dto.feed.StudyPeriodStatus
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentMakeStudy3Binding
import com.takseha.presentation.viewmodel.feed.MakeStudyViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MakeStudy3Fragment : Fragment() {
    private var _binding: FragmentMakeStudy3Binding? = null
    private val binding get() = _binding!!
    private val viewModel: MakeStudyViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            makeStudyBtn.setOnClickListener {
                viewModel.makeNewStudy()
                it.findNavController()
                    .navigate(R.id.action_makeStudy3Fragment_to_newStudyFragment)
            }
            exitBtn.setOnClickListener {
                requireActivity().finish()
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
            maxMemberText.text = studyInfo.maximumMember.toString()
            githubRepoLink.text = studyInfo.repositoryName
            commitRule.text =
                if (studyInfo.periodType == StudyPeriodStatus.STUDY_PERIOD_WEEK) getString(R.string.feed_rule_week) else if (studyInfo.periodType == StudyPeriodStatus.STUDY_PERIOD_EVERYDAY) getString(
                    R.string.feed_rule_everyday
                ) else getString(R.string.feed_rule_free)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}