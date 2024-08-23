package com.takseha.presentation.ui.mystudy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentDeleteAccountBinding
import com.takseha.presentation.databinding.FragmentQuitStudyBinding
import com.takseha.presentation.databinding.FragmentSettingHomeBinding
import com.takseha.presentation.ui.common.CustomCheckDialog
import com.takseha.presentation.viewmodel.mystudy.MyStudySettingViewModel
import com.takseha.presentation.viewmodel.profile.SettingHomeViewModel
import kotlinx.coroutines.launch

class QuitStudyFragment : Fragment() {
    private var _binding: FragmentQuitStudyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyStudySettingViewModel by viewModels()
    private var studyInfoId: Int = 0
    private lateinit var message: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuitStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        studyInfoId = requireActivity().intent.getIntExtra("studyInfoId", 0)

        with(binding) {
            backBtn.setOnClickListener {
                it.findNavController().popBackStack()
            }
            deleteReasonSelectRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                quitStudyBtn.isEnabled = true
                newReasonEditText.visibility = if (checkedId == reason4CheckBtn.id) VISIBLE else GONE
            }
            quitStudyBtn.setOnClickListener {
                message = setMessage()
                showDeleteAccountDialog(message)
            }
        }
    }

    private fun setMessage(): String {
        return with(binding) {
            when {
                reason1CheckBtn.isChecked -> getString(R.string.study_quit_reason1)
                reason2CheckBtn.isChecked -> getString(R.string.study_quit_reason2)
                reason3CheckBtn.isChecked -> getString(R.string.study_quit_reason3)
                else -> newReasonEditText.text.toString()
            }
        }
    }

    private fun showDeleteAccountDialog(message: String) {
        val customCheckDialog = CustomCheckDialog(requireContext())
        customCheckDialog.setAlertText(getString(R.string.study_quit_alert_title))
        customCheckDialog.setAlertDetailText(getString(R.string.study_quit_alert_detail))
        customCheckDialog.setCancelBtnText(getString(R.string.alert_logout_cancel))
        customCheckDialog.setConfirmBtnText(getString(R.string.alert_delete_account_confirm))
        customCheckDialog.setOnConfirmClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.withdrawStudy(studyInfoId)
                findNavController().navigate(R.id.action_quitStudyFragment_to_studyQuitCompleteFragment)
            }
        }
        customCheckDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}