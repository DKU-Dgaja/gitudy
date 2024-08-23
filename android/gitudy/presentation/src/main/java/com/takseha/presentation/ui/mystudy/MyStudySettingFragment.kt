package com.takseha.presentation.ui.mystudy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentMyStudyHomeBinding
import com.takseha.presentation.databinding.FragmentMyStudySettingBinding
import com.takseha.presentation.ui.common.CustomCheckDialog
import com.takseha.presentation.viewmodel.home.MainHomeViewModel
import com.takseha.presentation.viewmodel.mystudy.MyStudySettingViewModel
import com.takseha.presentation.viewmodel.profile.SettingHomeViewModel
import kotlinx.coroutines.launch

class MyStudySettingFragment : Fragment() {
    private var _binding: FragmentMyStudySettingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyStudySettingViewModel by viewModels()
    private var studyInfoId: Int = 0
    private var isLeader: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.WHITE)
        studyInfoId = requireActivity().intent.getIntExtra("studyInfoId", 0)
        isLeader = requireActivity().intent.getBooleanExtra("isLeader", false)

        with(binding) {
            if (isLeader!!) {
                studyQuitBtn.visibility = GONE
                studyEndBtn.visibility = VISIBLE
            } else {
                studyQuitBtn.visibility = VISIBLE
                studyEndBtn.visibility = GONE
            }
            backBtn.setOnClickListener {
                it.findNavController().popBackStack()
            }
            studyQuitBtn.setOnClickListener {
                it.findNavController().navigate(R.id.action_myStudySettingFragment_to_quitStudyFragment)
            }
            studyEndBtn.setOnClickListener {
            }
        }
    }

    private fun showStudyEndDialog(message: String) {
        val customCheckDialog = CustomCheckDialog(requireContext())
        customCheckDialog.setAlertText(getString(R.string.study_end_alert_title))
        customCheckDialog.setAlertDetailText(getString(R.string.study_end_alert_detail))
        customCheckDialog.setCancelBtnText(getString(R.string.alert_logout_cancel))
        customCheckDialog.setConfirmBtnText(getString(R.string.alert_delete_account_confirm))
        customCheckDialog.setOnConfirmClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
            }
        }
        customCheckDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}