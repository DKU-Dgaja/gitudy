package com.takseha.presentation.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentStudyApplyMessageBinding
import com.takseha.presentation.databinding.LayoutSnackbarGreyBinding
import com.takseha.presentation.ui.common.CustomSetDialog
import com.takseha.presentation.ui.common.KeyboardUtils
import com.takseha.presentation.viewmodel.feed.StudyApplyViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StudyApplyMessageFragment : Fragment() {
    private var _binding: FragmentStudyApplyMessageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudyApplyViewModel by activityViewModels()
    private var studyInfoId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.WHITE)
        studyInfoId = requireActivity().intent?.getIntExtra("studyInfoId", 0) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudyApplyMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)

        with(binding) {
            backBtn.setOnClickListener {
                it.findNavController().popBackStack()
            }
            applyBtn.setOnClickListener {
                val message = messageToCaptain.text.toString()
                showApplyStudyDialog(studyInfoId, "", message)
            }
            cancelBtn.setOnClickListener {
                it.findNavController().popBackStack()
            }
        }
    }

    private fun makeSnackBar(message: String): Snackbar {
        val snackBar = Snackbar.make(requireView(), "Grey SnackBar", Snackbar.LENGTH_SHORT)
        val binding = LayoutSnackbarGreyBinding.inflate(layoutInflater)

        @Suppress("RestrictedApi")
        val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout

        with(snackBarLayout) {
            removeAllViews()
            setPadding(22, 0, 22, 20)
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.TRANSPARENT))
            addView(binding.root, 0)
        }

        with(binding) {
            snackBarText.text = message
        }

        return snackBar
    }

    // TODO: 추후 코드 리팩토링 필요 -> collectLatest를 value로 바꿔도 될듯?
    private fun showApplyStudyDialog(studyInfoId: Int, joinCode: String, message: String) {
        val customSetDialog = CustomSetDialog(requireContext())
        customSetDialog.setAlertText(getString(R.string.feed_apply_study))
        customSetDialog.setOnConfirmClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.applyStudy(studyInfoId, "", message)
                viewModel.isApplySucceed.collectLatest {
                    if (it != null) {
                        if (it) {
                            binding.applyBtn.findNavController()
                                .navigate(R.id.action_studyApplyMessageFragment_to_newStudyApplyFragment)
                        } else {
                            viewModel.applyErrorMessage.collectLatest { errorMessage ->
                                if (errorMessage != null) {
                                    makeSnackBar(errorMessage).apply {
                                        anchorView = binding.applyBtn
                                    }.show()
                                    viewModel.resetApplyErrorMessage()
                                }
                            }
                        }
                    }
                }
            }
        }
        customSetDialog.show()
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