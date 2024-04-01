package com.takseha.presentation.ui.feed

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.takseha.presentation.R
import com.takseha.presentation.ui.auth.LoginActivity

class NewStudyFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Handler(Looper.getMainLooper()).postDelayed({
            requireActivity().finish()
        }, 2000)

        return inflater.inflate(R.layout.fragment_new_study, container, false)
    }
}