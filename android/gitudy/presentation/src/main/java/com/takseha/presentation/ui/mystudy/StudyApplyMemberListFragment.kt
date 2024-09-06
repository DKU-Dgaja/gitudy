package com.takseha.presentation.ui.mystudy

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.mystudy.StudyApplyMember
import com.takseha.presentation.R
import com.takseha.presentation.adapter.StudyApplyMemberListRVAdapter
import com.takseha.presentation.databinding.FragmentStudyApplyMemberListBinding
import com.takseha.presentation.viewmodel.mystudy.StudyApplyMemberListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StudyApplyMemberListFragment : Fragment() {
    private var _binding: FragmentStudyApplyMemberListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudyApplyMemberListViewModel by activityViewModels()
    private var studyInfoId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studyInfoId = arguments?.getInt("studyInfoId") ?: 0
        viewModel.getStudyApplyMemberList(studyInfoId, null, 50)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudyApplyMemberListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                if (it != null) {
                    binding.studyName.text = it.studyTopic
                    if (it.applyList.isNotEmpty()) {
                        binding.isNoApplyMemberLayout.visibility = GONE
                    } else {
                        binding.isNoApplyMemberLayout.visibility = VISIBLE
                    }
                    setStudyApplyMemberList(it.applyList)
                }
            }
        }
        with(binding) {
            applyMemberListSwipeRefreshLayout.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.getStudyApplyMemberList(studyInfoId, null, 50)
                    applyMemberListSwipeRefreshLayout.isRefreshing = false
                }
            }
            backBtn.setOnClickListener {
                it.findNavController().popBackStack()
            }
        }
    }

    // 원래 페이지로 돌아왔을 때 state 업데이트
    override fun onResume() {
        super.onResume()
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.WHITE)
        viewModel.getStudyApplyMemberList(studyInfoId, null, 50)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                Log.d("StudyApplyMemberProfileViewModel", it.toString())
            }
        }
    }

    private fun setStudyApplyMemberList(studyApplyMemberList: List<StudyApplyMember>) {
        with(binding) {
            val studyApplyMemberListRVAdapter =
                StudyApplyMemberListRVAdapter(requireContext(), studyApplyMemberList)
            applyMemberList.adapter = studyApplyMemberListRVAdapter
            applyMemberList.layoutManager = LinearLayoutManager(requireContext())

            clickNoticeItem(studyApplyMemberListRVAdapter, studyApplyMemberList)
        }
    }

    private fun clickNoticeItem(
        studyApplyMemberListRVAdapter: StudyApplyMemberListRVAdapter,
        studyApplyMemberList: List<StudyApplyMember>
    ) {
        studyApplyMemberListRVAdapter.onClickListener =
            object : StudyApplyMemberListRVAdapter.OnClickListener {
                override fun onClick(view: View, position: Int) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        val bundle = Bundle().apply {
                            putSerializable("memberProfile", studyApplyMemberList[position])
                            putInt("studyInfoId", studyInfoId)
                        }
                        view.findNavController().navigate(R.id.action_studyApplyMemberListFragment_to_studyApplyMemberProfileFragment, bundle)
                    }, 200)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}