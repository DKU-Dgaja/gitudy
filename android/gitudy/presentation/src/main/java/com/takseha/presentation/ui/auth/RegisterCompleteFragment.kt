package com.takseha.presentation.ui.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.activityViewModels
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentRegisterCompleteBinding
import com.takseha.presentation.ui.home.MainHomeActivity
import com.takseha.presentation.viewmodel.auth.RegisterViewModel

// TODO : push 알림 수신 동의 현황 dialog로 띄우기!!! 회원가입 후? 알아보기!
class RegisterCompleteFragment : Fragment() {
    private var _binding : FragmentRegisterCompleteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor =  Color.argb(0xFF,0x15,0x14,0x1B)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            nickname.text = viewModel.registerInfoState.value.name

            val characterAnim = AnimationUtils.loadAnimation(context, R.anim.fade_in)
            characterLayout.startAnimation(characterAnim)
            val textAnim = AnimationUtils.loadAnimation(context, R.anim.alpha)
            loginCompleteDescription1.startAnimation(textAnim)
            loginCompleteDescription2.startAnimation(textAnim)
            loginCompleteDescription3.startAnimation(textAnim)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(requireActivity(), MainHomeActivity::class.java))
            requireActivity().finish()
        }, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}