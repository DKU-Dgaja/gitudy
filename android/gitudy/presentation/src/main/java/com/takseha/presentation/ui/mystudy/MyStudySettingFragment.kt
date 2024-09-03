package com.takseha.presentation.ui.mystudy

import android.os.Bundle
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
import androidx.navigation.fragment.findNavController
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentMyStudySettingBinding
import com.takseha.presentation.ui.common.CustomCheckDialog
import com.takseha.presentation.viewmodel.mystudy.MyStudySettingViewModel
import kotlinx.coroutines.launch

class MyStudySettingFragment : Fragment() {
    private var _binding: FragmentMyStudySettingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyStudySettingViewModel by activityViewModels()
    private var studyInfoId: Int = 0
    private var isLeader: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.WHITE)
        studyInfoId = requireActivity().intent.getIntExtra("studyInfoId", 0)
        isLeader = requireActivity().intent.getBooleanExtra("isLeader", false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyStudySettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            if (isLeader!!) {
                studyQuitBtn.visibility = GONE
                studyApplyMemberManageBtn.visibility = VISIBLE
                studyEndBtn.visibility = VISIBLE
                studyDeleteBtn.visibility = VISIBLE
                memberSettingBtn.visibility = VISIBLE
            } else {
                studyQuitBtn.visibility = VISIBLE
                studyApplyMemberManageBtn.visibility = GONE
                studyEndBtn.visibility = GONE
                studyDeleteBtn.visibility = GONE
                memberSettingBtn.visibility = GONE
            }
            backBtn.setOnClickListener {
                it.findNavController().popBackStack()
            }
            mainSettingBtn.setOnClickListener {
                it.findNavController().navigate(R.id.action_myStudySettingFragment_to_myStudyDefaultSettingFragment)
            }
            memberSettingBtn.setOnClickListener {
                it.findNavController().navigate(R.id.action_myStudySettingFragment_to_myStudySettingMemberFragment)
            }
            studyApplyMemberManageBtn.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt("studyInfoId", studyInfoId)
                }
                it.findNavController().navigate(R.id.action_myStudySettingFragment_to_studyApplyMemberListFragment, bundle)
            }
            studyQuitBtn.setOnClickListener {
                it.findNavController().navigate(R.id.action_myStudySettingFragment_to_quitStudyFragment)
            }
            studyDeleteBtn.setOnClickListener {
                showStudyDeleteDialog(studyInfoId)
            }
            studyEndBtn.setOnClickListener {
                showStudyEndDialog(studyInfoId)
            }
        }
    }

    private fun showStudyDeleteDialog(studyInfoId: Int) {
        val customCheckDialog = CustomCheckDialog(requireContext())
        customCheckDialog.setAlertText(getString(R.string.study_delete_alert_title))
        customCheckDialog.setAlertDetailText(getString(R.string.study_delete_alert_detail))
        customCheckDialog.setCancelBtnText("취소")
        customCheckDialog.setConfirmBtnText("삭제하기")
        customCheckDialog.setOnConfirmClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.deleteStudy(studyInfoId)
                requireActivity().finish()
            }
        }
        customCheckDialog.show()
    }

    private fun showStudyEndDialog(studyInfoId: Int) {
        val customCheckDialog = CustomCheckDialog(requireContext())
        customCheckDialog.setAlertText(getString(R.string.study_end_alert_title))
        customCheckDialog.setAlertDetailText(getString(R.string.study_end_alert_detail))
        customCheckDialog.setCancelBtnText("취소")
        customCheckDialog.setConfirmBtnText("종료하기")
        customCheckDialog.setOnConfirmClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.endStudy(studyInfoId)
                requireActivity().finish()
            }
        }
        customCheckDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}