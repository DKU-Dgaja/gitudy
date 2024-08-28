package com.takseha.presentation.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.takseha.presentation.databinding.FragmentCommitWebViewBinding

class CommitWebViewFragment : Fragment() {
    private var _binding : FragmentCommitWebViewBinding? = null
    private val binding get() = _binding!!

    private var githubUrl: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            githubUrl = it.getString("githubUrl")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommitWebViewBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.commitWebView.loadUrl(githubUrl!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}