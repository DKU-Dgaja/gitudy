package com.takseha.presentation.ui.auth

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentPopupAgreementBinding
import com.takseha.presentation.viewmodel.auth.PopupAgreementIntent
import com.takseha.presentation.viewmodel.auth.PopupAgreementViewModel
import kotlinx.coroutines.launch


// MVI 구조
class PopupAgreementFragment : Fragment() {
    private var _binding : FragmentPopupAgreementBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PopupAgreementViewModel

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
        viewModel = ViewModelProvider(this)[PopupAgreementViewModel::class.java]

        view.setBackgroundColor(Color.argb(0x80,0x27,0x29,0x2E))
        requireActivity().window.statusBarColor = Color.argb(0x80,0x27,0x29,0x2E)

//        Handler(Looper.getMainLooper()).postDelayed({
//            view.setBackgroundColor(Color.argb(0x80,0x27,0x29,0x2E))
//            requireActivity().window.statusBarColor = Color.argb(0x80,0x27,0x29,0x2E)
//        }, 100)

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
            checkBtn4.setOnClickListener {
                viewModel.handleIntent(PopupAgreementIntent.CheckCheckBtn4)
            }

            detailBtn1.setOnClickListener {
                it.findNavController().navigate(R.id.action_popupFragment_to_popupDetail1Fragment)
            }
            detailBtn2.setOnClickListener {
                it.findNavController().navigate(R.id.action_popupFragment_to_popupDetail2Fragment)
            }
            detailBtn3.setOnClickListener {
                it.findNavController().navigate(R.id.action_popupFragment_to_popupDetail3Fragment)
            }

            confirmBtn.setOnClickListener {
                it.findNavController().navigate(R.id.action_popupFragment_to_inputNicknameFragment)
            }
        }
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(
                lifecycle = viewLifecycleOwner.lifecycle,
                minActiveState = Lifecycle.State.STARTED
            ).collect {
                handleAgreementStates(it.isCheckBtn1Checked, it.isCheckBtn2Checked, it.isCheckBtn3Checked, it.isCheckBtn4Checked)
            }
        }
    }

    private fun handleAgreementStates(
        isCheckBtn1Checked: Boolean,
        isCheckBtn2Checked: Boolean,
        isCheckBtn3Checked: Boolean,
        isCheckBtn4Checked: Boolean
    ) {
        with(binding) {
            val isAllChecked = isCheckBtn1Checked && isCheckBtn2Checked && isCheckBtn3Checked && isCheckBtn4Checked

            setAllAgreeBtnState(isAllChecked)
            checkBtn1.isChecked = isCheckBtn1Checked
            checkBtn2.isChecked = isCheckBtn2Checked
            checkBtn3.isChecked = isCheckBtn3Checked
            checkBtn4.isChecked = isCheckBtn4Checked
            confirmBtn.isEnabled = isAllChecked
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