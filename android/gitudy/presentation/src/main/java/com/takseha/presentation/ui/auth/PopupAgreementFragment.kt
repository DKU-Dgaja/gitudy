package com.takseha.presentation.ui.auth

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentPopupAgreementBinding
import com.takseha.presentation.ui.common.SnackBarHelper
import com.takseha.presentation.viewmodel.auth.PopupAgreementIntent
import com.takseha.presentation.viewmodel.auth.PopupAgreementViewModel
import com.takseha.presentation.viewmodel.common.BaseApplicationViewModel
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


// MVI 구조
class PopupAgreementFragment : Fragment() {
    private var _binding : FragmentPopupAgreementBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PopupAgreementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPopupAgreementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(Color.argb(0x80,0x27,0x29,0x2E))
        requireActivity().window.statusBarColor = Color.argb(0x80,0x27,0x29,0x2E)

        collectFlows()

        with(binding) {
            exitBtn.setOnClickListener {
                view.setBackgroundColor(Color.TRANSPARENT)
                requireActivity().finish()
            }

            allAgreeBtn.setOnClickListener {
                viewModel.handleIntent(PopupAgreementIntent.CheckAllAgreeBtn)
            }
            checkBtn1.setOnClickListener {
                viewModel.handleIntent(PopupAgreementIntent.CheckCheckBtn1)
            }
            checkBtn2.setOnClickListener {
                viewModel.handleIntent(PopupAgreementIntent.CheckCheckBtn2)
            }
            checkBtn3.setOnClickListener {
                viewModel.handleIntent(PopupAgreementIntent.CheckCheckBtn3)
            }
            pushAlarmYnBtn.setOnClickListener {
                viewModel.handleIntent(PopupAgreementIntent.PushAlarmYnBtnChecked)
            }

            detailBtn1.setOnClickListener {
                it.findNavController().navigate(R.id.action_popupFragment_to_popupDetail1Fragment)
            }
            detailBtn2.setOnClickListener {
                it.findNavController().navigate(R.id.action_popupFragment_to_popupDetail2Fragment)
            }
            confirmBtn.setOnClickListener {
                var action = PopupAgreementFragmentDirections.actionPopupFragmentToInputNicknameFragment(pushAlarmYnBtn.isChecked)
                it.findNavController().navigate(action)
            }
        }
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(
                lifecycle = viewLifecycleOwner.lifecycle,
                minActiveState = Lifecycle.State.STARTED
            ).collectLatest {
                handleAgreementStates(it.isCheckBtn1Checked, it.isCheckBtn2Checked, it.isCheckBtn3Checked, it.isPushAlarmYnBtnChecked)
            }
        }
    }

    private fun handleAgreementStates(
        isCheckBtn1Checked: Boolean,
        isCheckBtn2Checked: Boolean,
        isCheckBtn3Checked: Boolean,
        isPushAlarmYnBtnChecked: Boolean
    ) {
        with(binding) {
            val isAllChecked = isCheckBtn1Checked && isCheckBtn2Checked && isCheckBtn3Checked && isPushAlarmYnBtnChecked
            val isOk = isCheckBtn1Checked && isCheckBtn2Checked && isCheckBtn3Checked

            setAllAgreeBtnState(isAllChecked)
            checkBtn1.isChecked = isCheckBtn1Checked
            checkBtn2.isChecked = isCheckBtn2Checked
            checkBtn3.isChecked = isCheckBtn3Checked
            pushAlarmYnBtn.isChecked = isPushAlarmYnBtnChecked
            confirmBtn.isEnabled = isOk
        }
    }

    private fun setAllAgreeBtnState(isAllChecked: Boolean) {
        with(binding) {
            if (isAllChecked) {
                allAgreeBtn.setBackgroundResource(R.drawable.box_non_stroke_r8_green)
                allAgreeCheckIcon.setImageResource(R.drawable.ic_check_black_c)
                allAgreeCheckText.setTextColor(ContextCompat.getColor(requireContext(), R.color.GS_900))
            } else {
                allAgreeBtn.setBackgroundResource(R.drawable.box_stroke_200_r8)
                allAgreeCheckIcon.setImageResource(R.drawable.ic_check_grey_c)
                allAgreeCheckText.setTextColor(ContextCompat.getColor(requireContext(), R.color.GS_400))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}