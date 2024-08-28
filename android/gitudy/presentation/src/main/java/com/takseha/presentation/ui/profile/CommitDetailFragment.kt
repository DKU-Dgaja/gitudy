package com.takseha.presentation.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.takseha.data.dto.mystudy.Commit
import com.takseha.data.dto.mystudy.CommitStatus
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentCommitDetailBinding
import com.takseha.presentation.viewmodel.mystudy.CommitDetailViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CommitDetailFragment : Fragment() {
    private var _binding : FragmentCommitDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CommitDetailViewModel by activityViewModels()
    private var studyInfoId: Int = 0
    private var isLeader: Boolean? = null
    private var commit: Commit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studyInfoId = requireActivity().intent.getIntExtra("studyInfoId", 0)
        isLeader = requireActivity().intent.getBooleanExtra("isLeader", false)
        arguments?.let {
            commit = it.getSerializable("commit") as Commit?
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
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.BACKGROUND_BLACK)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getRepositoryInfo(studyInfoId)

            viewModel.repositoryInfoState.collectLatest { repositoryInfo ->
                with(binding) {
                    commitTitle.text = commit?.message
                    commitInfo.text = getString(R.string.study_to_do_commit_info, commit?.name, commit?.commitDate,)
                    when (commit?.status) {
                        CommitStatus.COMMIT_APPROVAL -> commitStatus.text = "승인완료"
                        CommitStatus.COMMIT_DELETE -> {
                            commitStatus.text = "커밋삭제"
                            commitStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.GS_500))
                        }
                        CommitStatus.COMMIT_REJECTION -> {
                            commitStatus.text = "승인반려"
                            commitStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.BASIC_RED))
                        }
                        CommitStatus.COMMIT_WAITING -> {
                            commitStatus.text = "승인대기"
                            commitStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.BASIC_GREEN))
                        }
                        null -> commitStatus.text = ""
                    }
                    Glide.with(this@CommitDetailFragment)
                        .load(commit?.profileImageUrl)
                        .error(R.drawable.logo_profile_default)
                        .into(profileImg)
                    nickname.text = commit?.name
                    githubLinkBtn.setOnClickListener {
                        val githubUrl = getString(R.string.commit_github_url, repositoryInfo.owner, repositoryInfo.name, commit?.commitSha)
                        val bundle = Bundle().apply {
                            putString("githubUrl", githubUrl)
                        }
                        it.findNavController().navigate(R.id.action_commitDetailFragment_to_commitWebViewFragment, bundle)
                    }
                    backBtn.setOnClickListener {
                        it.findNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}