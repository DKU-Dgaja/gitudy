package com.takseha.presentation.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.takseha.presentation.databinding.FragmentMyCommitBinding
import com.takseha.presentation.viewmodel.profile.MyCommitViewModel

class MyCommitFragment : Fragment() {
    private var _binding: FragmentMyCommitBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyCommitViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyCommitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backBtn.setOnClickListener { requireActivity().finish() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}