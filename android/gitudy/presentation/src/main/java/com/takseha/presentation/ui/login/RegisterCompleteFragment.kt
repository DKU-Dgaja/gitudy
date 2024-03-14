package com.takseha.presentation.ui.login

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.takseha.common.model.SPKey
import com.takseha.common.util.SP
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentRegisterCompleteBinding

class RegisterCompleteFragment : Fragment() {
    private var _binding : FragmentRegisterCompleteBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: SP

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

        prefs = SP(requireActivity().applicationContext)

        with(binding) {
            nickname.text = prefs.loadPref(SPKey.GITUDY_NAME, "0")

            val characterAnim = AnimationUtils.loadAnimation(context, R.anim.fade_in)
            characterLayout.startAnimation(characterAnim)
            val textAnim = AnimationUtils.loadAnimation(context, R.anim.alpha)
            loginCompleteDescription1.startAnimation(textAnim)
            loginCompleteDescription2.startAnimation(textAnim)
            loginCompleteDescription3.startAnimation(textAnim)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}