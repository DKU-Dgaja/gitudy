package com.takseha.presentation.ui.feed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.feed.StudyInfo
import com.takseha.data.dto.mystudy.MyStudyWithTodo
import com.takseha.presentation.R
import com.takseha.presentation.adapter.FeedRVAdapter
import com.takseha.presentation.adapter.MyStudyRVAdapter
import com.takseha.presentation.databinding.FragmentFeedHomeBinding
import com.takseha.presentation.ui.mystudy.MyStudyMainActivity
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

        with(binding) {
            makeNewStudyBtn.setOnClickListener {
                startActivity(Intent(activity, MakeStudyActivity::class.java))
            }

//            // 스크롤 끝나면 다음 리스트 불러오기 기능 구현(무한스크롤)
//            viewModel.cursorIdxRes.observe(viewLifecycleOwner) {
//                viewModel.getFeedList(it, 5, "createdDateTime")
//            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getFeedList(null, 10, "createdDateTime")
                viewModel.uiState.collectLatest {
                    if (it.studyInfoList.isNotEmpty()) {
                        val feedRVAdapter = FeedRVAdapter(requireContext(), it.studyInfoList)

                        if (feedRVAdapter.itemCount == 0) {
                            isNoStudyLayout.visibility = View.VISIBLE
                        }

                        feedList.adapter = feedRVAdapter
                        feedList.layoutManager = LinearLayoutManager(requireContext())

                        clickFeedItem(feedRVAdapter, it.studyInfoList)
                    }
                }
            }

            swipeRefreshFeedList.setOnRefreshListener {
                viewModel.getFeedList(null, 10, "createdDateTime")
                swipeRefreshFeedList.isRefreshing = false
            }
        }
    }

    private fun clickFeedItem(feedRVAdapter: FeedRVAdapter, studyList: List<StudyInfo>) {
        feedRVAdapter.itemClick = object : FeedRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val intent = Intent(requireContext(), StudyApplyActivity::class.java)
                intent.putExtra("studyInfoId", studyList[position].id)
                intent.putExtra("studyImgColor", studyList[position].profileImageUrl)
                Log.d("FeedHomeFragment", intent.extras.toString())
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}