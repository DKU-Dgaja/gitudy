package com.takseha.presentation.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.takseha.data.dto.mystudy.Commit
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentCommitDetailBinding

class CommitDetailFragment : Fragment() {
    private var _binding : FragmentCommitDetailBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_COMMIT = "commit"

        fun newInstance(commit: Commit): CommitDetailFragment {
            val fragment = CommitDetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_COMMIT, commit)
            fragment.arguments = args
            return fragment
        }
    }

    private var commit: Commit? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            commit = it.getSerializable(ARG_COMMIT) as Commit
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommitDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = Color.argb(0x80,0x27,0x29,0x2E)

        with(binding) {
            commitTitle.text = commit!!.message
            Glide.with(this@CommitDetailFragment)
                .load("https://avatars.githubusercontent.com/u/86196342?v=4")
                .error(R.drawable.logo_profile_default)
                .into(profileImg)
            nickname.text = commit!!.rejectionReason
            githubLinkBtn.setOnClickListener {
                it.findNavController().navigate(R.id.action_myCommitFragment_to_commitWebViewFragment)
            }
            backBtn.setOnClickListener {
                requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.WHITE)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}