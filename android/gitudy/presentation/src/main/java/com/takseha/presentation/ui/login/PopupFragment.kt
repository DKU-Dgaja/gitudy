package com.takseha.presentation.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentAgreementPopupBinding

class PopupFragment : Fragment() {
    private lateinit var binding: FragmentAgreementPopupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_agreement_popup, container, false)


        return view
    }
}