package com.takseha.presentation.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentAgreementPopupBinding


class PopupFragment : Fragment() {
    private var _binding : FragmentAgreementPopupBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAgreementPopupBinding.inflate(inflater, container, false)
        Log.d("PopupFragment", "onCreateView 성공")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var isAllChecked = false

        Handler(Looper.getMainLooper()).postDelayed({
            view.setBackgroundColor(Color.argb(0x80,0x27,0x29,0x2E))
        }, 300)

        with(binding) {
            exitBtn.setOnClickListener {
                view.setBackgroundColor(Color.TRANSPARENT)
                requireActivity().finish()
            }
            allAgreeBtn.setOnClickListener {
                if (!isAllChecked) {
                    it.setBackgroundResource(R.drawable.box_non_stroke_r8_green)
                    allAgreeCheckIcon.setImageResource(R.drawable.ic_check_black_c)
                    allAgreeCheckText.setTextColor(ContextCompat.getColor(requireContext(), R.color.GS_900))

                    checkAllBtn(true)

                    isAllChecked = true
                } else {
                    it.setBackgroundResource(R.drawable.box_stroke_200_r8)
                    allAgreeCheckIcon.setImageResource(R.drawable.ic_check_grey_c)
                    allAgreeCheckText.setTextColor(ContextCompat.getColor(requireContext(), R.color.GS_400))

                    checkAllBtn(false)

                    isAllChecked = false
                }
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

            if (checkBtn1.isChecked && checkBtn2.isChecked && checkBtn3.isChecked && checkBtn4.isChecked) confirmBtn.isEnabled = true

            confirmBtn.setOnClickListener {
                it.findNavController().navigate(R.id.action_popupFragment_to_inputIdFragment)
            }
        }
        Log.d("PopupFragment", "onViewCreated 성공")
    }

    private fun checkAllBtn(isChecked: Boolean) {
        with(binding) {
            checkBtn1.isChecked = isChecked
            checkBtn2.isChecked = isChecked
            checkBtn3.isChecked = isChecked
            checkBtn4.isChecked = isChecked

        }
    }

    private fun checkEachBtn(checkedDetail: Bundle) {
        with(binding) {
            when (checkedDetail.getString("isChecked")) {
                "detail1" -> checkBtn2.isChecked = true
                "detail2" -> checkBtn3.isChecked = true
                "detail3" -> checkBtn4.isChecked = true
                else -> null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}