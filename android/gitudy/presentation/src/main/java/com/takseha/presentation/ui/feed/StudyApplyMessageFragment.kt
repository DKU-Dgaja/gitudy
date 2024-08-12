package com.takseha.presentation.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentStudyApplyMessageBinding
import com.takseha.presentation.databinding.LayoutSnackbarGreyBinding
import com.takseha.presentation.viewmodel.feed.StudyApplyViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StudyApplyMessageFragment : Fragment() {
    private var _binding: FragmentStudyApplyMessageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudyApplyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val studyInfoId = activity?.intent?.getIntExtra("studyInfoId", 0) ?: 0
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.WHITE)

        with(binding) {
            backBtn.setOnClickListener {
                it.findNavController().popBackStack()
            }
            applyBtn.setOnClickListener {
                val message = messageToCaptain.text.toString()
                viewModel.applyStudy(studyInfoId, "", message)
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.isApplySucceed.collectLatest {
                        if (it != null) {
                            if (it) {
                                applyBtn.findNavController()
                                    .navigate(R.id.action_studyApplyMessageFragment_to_newStudyApplyFragment)
                            } else {
                                makeSnackBar(getString(R.string.alert_study_apply_is_not_ok)).apply {
                                    anchorView = applyBtn
                                }.show()
                            }
                        }
                    }
                }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}