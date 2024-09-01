package com.takseha.presentation.ui.mystudy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.takseha.presentation.databinding.FragmentRecommendNewStudyBinding
import com.takseha.presentation.ui.feed.MakeStudyActivity

class RecommendNewStudyFragment : Fragment() {
    private var _binding: FragmentRecommendNewStudyBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecommendNewStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            exitBtn.setOnClickListener { requireActivity().finish() }
            makeStudyBtn.setOnClickListener {
                val intent = Intent(requireContext(), MakeStudyActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            noThanksBtn.setOnClickListener { requireActivity().finish() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}