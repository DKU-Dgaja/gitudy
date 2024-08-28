package com.takseha.presentation.ui.profile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.takseha.presentation.R
import com.takseha.presentation.ui.SplashActivity

class LogoutCompleteFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(requireActivity(), SplashActivity::class.java))
            requireActivity().finish()
        }, 2000)

        return inflater.inflate(R.layout.fragment_logout_complete, container, false)
    }
}