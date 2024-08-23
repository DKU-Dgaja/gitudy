package com.takseha.presentation.ui.mystudy

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.takseha.presentation.R
import com.takseha.presentation.ui.SplashActivity

class StudyQuitCompleteFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_studyQuitCompleteFragment_to_recommandNewStudyFragment)
        }, 2000)

        return inflater.inflate(R.layout.fragment_logout_complete, container, false)
    }
}