package com.takseha.presentation.ui.mystudy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentQuitStudyBinding
import com.takseha.presentation.ui.common.CustomCheckDialog
import com.takseha.presentation.ui.common.KeyboardUtils
import com.takseha.presentation.viewmodel.mystudy.MyStudySettingViewModel
import kotlinx.coroutines.launch

class QuitStudyFragment : Fragment() {
    private var _binding: FragmentQuitStudyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyStudySettingViewModel by activityViewModels()
    private var studyInfoId: Int = 0
    private lateinit var message: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studyInfoId = requireActivity().intent.getIntExtra("studyInfoId", 0)
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
        setupUI(view)

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

    private fun setupUI(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    activity?.let { KeyboardUtils.hideKeyboard(it) }
                }
                false
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}