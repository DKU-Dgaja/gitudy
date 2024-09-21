package com.takseha.presentation.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.profile.Bookmark
import com.takseha.data.dto.profile.CommitWithStudyName
import com.takseha.presentation.R
import com.takseha.presentation.adapter.BookmarkListRVAdapter
import com.takseha.presentation.adapter.MyCommitListRVAdapter
import com.takseha.presentation.databinding.FragmentMyCommitBinding
import com.takseha.presentation.ui.feed.StudyApplyActivity
import com.takseha.presentation.viewmodel.profile.ProfileHomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyCommitFragment : Fragment() {
    private var _binding: FragmentMyCommitBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileHomeViewModel by activityViewModels()

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
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.WHITE)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getMyCommitLists(null, null, 50)
            viewModel.myCommitsState.collectLatest {
                if (!it.isMyCommitEmpty) {
                    binding.isNoCommitLayout.visibility = View.GONE
                } else {
                    binding.isNoCommitLayout.visibility = View.VISIBLE
                }
                setMyCommitList(it.commitList)
            }
        }
        binding.backBtn.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun setMyCommitList(commits: List<CommitWithStudyName>) {
        with(binding) {
            val myCommitListRVAdapter = MyCommitListRVAdapter(requireContext(), commits)

            commitList.adapter = myCommitListRVAdapter
            commitList.layoutManager = LinearLayoutManager(requireContext())

            clickMyCommitItem(myCommitListRVAdapter, commits)
        }
    }

    private fun clickMyCommitItem(
        myCommitListRVAdapter: MyCommitListRVAdapter,
        commits: List<CommitWithStudyName>
    ) {
        myCommitListRVAdapter.onClickListener = object : MyCommitListRVAdapter.OnClickListener {
            override fun onClick(view: View, position: Int) {
                val bundle = Bundle().apply {
                    putSerializable("commit", commits[position].commit)
                }
                view.findNavController().navigate(R.id.action_myCommitFragment_to_commitDetailFragment, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}