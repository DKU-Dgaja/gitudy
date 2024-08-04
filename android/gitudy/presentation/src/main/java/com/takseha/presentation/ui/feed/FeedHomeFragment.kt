package com.takseha.presentation.ui.feed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.feed.StudyInfo
import com.takseha.presentation.R
import com.takseha.presentation.adapter.FeedRVAdapter
import com.takseha.presentation.databinding.FragmentFeedHomeBinding
import com.takseha.presentation.viewmodel.feed.FeedHomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// TODO: 정렬 버튼 선택 시 정렬 기준 변경, 참여 가능한 스터디만 보기 기능 구현(currentMem이랑 maximumMem이 같은 스터디 제외시키기)

class FeedHomeFragment : Fragment() {
    private var _binding: FragmentFeedHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FeedHomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.BACKGROUND)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getFeedList(null, 10, "createdDateTime")
            viewModel.uiState.collectLatest {
                binding.feedCnt.text = it.studyCnt.toString()
                if (!it.isFeedEmpty) {
                    binding.isNoStudyLayout.visibility = View.GONE
                    setFeedList(it.studyInfoList, it.studyCategoryMappingMap)
                } else {
                    binding.isNoStudyLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    // 원래 페이지로 돌아왔을 때 state 업데이트
    override fun onResume() {
        super.onResume()
        viewModel.getFeedList(null, 10, "createdDateTime")
    }

    private fun setFeedList(studyList: List<StudyInfo>, studyCategoryMappingMap: Map<Int, List<String>>) {
        with(binding) {
            val feedRVAdapter = FeedRVAdapter(requireContext(), studyList, studyCategoryMappingMap)

            feedList.adapter = feedRVAdapter
            feedList.layoutManager = LinearLayoutManager(requireContext())

            clickFeedItem(feedRVAdapter, studyList)
        }
    }

    private fun clickFeedItem(feedRVAdapter: FeedRVAdapter, studyList: List<StudyInfo>) {
        feedRVAdapter.itemClick = object : FeedRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val intent = Intent(requireContext(), StudyApplyActivity::class.java)
                intent.putExtra("studyInfoId", studyList[position].id)
                intent.putExtra("studyImgColor", studyList[position].profileImageUrl)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}