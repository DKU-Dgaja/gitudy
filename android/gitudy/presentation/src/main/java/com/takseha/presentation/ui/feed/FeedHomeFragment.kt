package com.takseha.presentation.ui.feed

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.presentation.R
import com.takseha.presentation.adapter.FeedRVAdapter
import com.takseha.presentation.databinding.FragmentFeedHomeBinding
import com.takseha.presentation.viewmodel.feed.FeedHomeViewModel
import com.takseha.presentation.viewmodel.feed.StudyInfoWithBookmarkStatus
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
        viewModel.getFeedList(null, 50, "createdDateTime")
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
            viewModel.uiState.collectLatest {
                with(binding) {
                    if (it.studyCnt == null) {
                        loadingImg.visibility = VISIBLE
                        feedCnt.text = ""
                    } else {
                        loadingImg.visibility = GONE
                        feedCnt.text = it.studyCnt.toString()
                    }
                    if (!it.isFeedEmpty) {
                        isNoStudyLayout.visibility = GONE
                    } else {
                        isNoStudyLayout.visibility = VISIBLE
                    }
                    setFeedList(it.studyInfoList, it.studyCategoryMappingMap)
                }
            }
        }
        binding.feedSwipeRefreshLayout.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getFeedList(null, 50, "createdDateTime")
                binding.feedSwipeRefreshLayout.isRefreshing = false
            }
        }

        binding.makeNewStudyBtn.setOnClickListener {
            val intent = Intent(requireContext(), MakeStudyActivity::class.java)
            intent.putExtra("studyCnt", viewModel.uiState.value.studyCnt)
            startActivity(intent)
        }
    }


    // 원래 페이지로 돌아왔을 때 state 업데이트
    override fun onResume() {
        super.onResume()
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.BACKGROUND)
        viewModel.getFeedList(null, 10, "createdDateTime")
    }

    private fun setFeedList(
        studyList: List<StudyInfoWithBookmarkStatus>,
        studyCategoryMappingMap: Map<Int, List<String>>
    ) {
        with(binding) {
            val feedRVAdapter =
                FeedRVAdapter(requireContext(), studyList, studyCategoryMappingMap)

            feedList.adapter = feedRVAdapter
            feedList.layoutManager = LinearLayoutManager(requireContext())

            clickFeedItem(feedRVAdapter, studyList)
        }
    }

    private fun clickFeedItem(
        feedRVAdapter: FeedRVAdapter,
        studyList: List<StudyInfoWithBookmarkStatus>
    ) {
        feedRVAdapter.onClickListener = object : FeedRVAdapter.OnClickListener {
            override fun onClick(view: View, position: Int) {
                val intent = Intent(requireContext(), StudyApplyActivity::class.java)
                intent.putExtra("studyInfoId", studyList[position].studyInfo.id)
                intent.putExtra("studyImgColor", studyList[position].studyInfo.profileImageUrl)
                startActivity(intent)
            }

            override fun bookmarkClick(view: View, position: Int) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.setBookmarkStatus(studyList[position].studyInfo.id)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}