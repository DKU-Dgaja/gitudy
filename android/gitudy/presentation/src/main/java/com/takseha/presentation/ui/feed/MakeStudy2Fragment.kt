package com.takseha.presentation.ui.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.takseha.data.dto.feed.StudyPeriodStatus
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentMakeStudy2Binding
import com.takseha.presentation.viewmodel.feed.MakeStudyViewModel

class MakeStudy2Fragment : Fragment() {
    private var _binding: FragmentMakeStudy2Binding? = null
    private val binding get() = _binding!!
    private val viewModel: MakeStudyViewModel by activityViewModels()
    private var memberNum: Int = 0
    private var studyCnt: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studyCnt = requireActivity().intent.getIntExtra("studyCnt", 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMakeStudy2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageIdx = studyCnt % 10

        with(binding) {
            memberNumber.text =
                String.format(getString(R.string.feed_member_full_number), memberNum)

            setMaxMember()
            commitCntSelectRadioGroup.setOnCheckedChangeListener { _, _ ->
                nextBtn.isEnabled = isStudyOpenRadioGroup.checkedRadioButtonId != -1 && memberNum != 0
                Log.d("MakeStudy2Fragment", "${commitCntSelectRadioGroup.checkedRadioButtonId }, ${isStudyOpenRadioGroup.checkedRadioButtonId }, ${memberNum}")
            }
            isStudyOpenRadioGroup.setOnCheckedChangeListener { _, _ ->
                nextBtn.isEnabled =
                    commitCntSelectRadioGroup.checkedRadioButtonId != -1 && memberNum != 0
            }
            nextBtn.setOnClickListener {
                val commitTime =
                    if (freeCheck.isChecked) StudyPeriodStatus.STUDY_PERIOD_NONE else if (oneDayCheck.isChecked) StudyPeriodStatus.STUDY_PERIOD_WEEK else StudyPeriodStatus.STUDY_PERIOD_EVERYDAY
                val isPublic =
                    if (publicCheck.isChecked) StudyStatus.STUDY_PUBLIC else StudyStatus.STUDY_PRIVATE

                viewModel.setStudyRule(commitTime, isPublic, memberNum, imageIdx.toString())
                val bundle = Bundle().apply {
                    putStringArrayList("categories", arguments?.getStringArrayList("categories"))
                }
                it.findNavController()
                    .navigate(R.id.action_makeStudy2Fragment_to_makeStudy3Fragment, bundle)
            }
            exitBtn.setOnClickListener {
                requireActivity().finish()
            }
        }
    }

    private fun setMaxMember() {
        with(binding) {
            memberMinusBtn.setOnClickListener {
                if (memberNum > 0) memberNum--
                memberNumber.text =
                    String.format(getString(R.string.feed_member_full_number), memberNum)
                nextBtn.isEnabled =
                    commitCntSelectRadioGroup.checkedRadioButtonId != -1 && isStudyOpenRadioGroup.checkedRadioButtonId != -1 && memberNum != 0
            }
            memberPlusBtn.setOnClickListener {
                if (memberNum < 10) memberNum++
                memberNumber.text =
                    String.format(getString(R.string.feed_member_full_number), memberNum)
                nextBtn.isEnabled =
                    commitCntSelectRadioGroup.checkedRadioButtonId != -1 && isStudyOpenRadioGroup.checkedRadioButtonId != -1 && memberNum != 0
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}