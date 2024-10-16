package com.takseha.presentation.ui.feed

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.presentation.R
import com.takseha.presentation.adapter.FeedRVAdapter
import com.takseha.presentation.databinding.FragmentFeedHomeBinding
import com.takseha.presentation.ui.home.MainHomeAlertActivity
import com.takseha.presentation.viewmodel.feed.FeedHomeViewModel
import com.takseha.presentation.viewmodel.feed.StudyInfoWithBookmarkStatus
import com.takseha.presentation.viewmodel.home.MyStudyWithTodo
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FeedHomeFragment : Fragment() {
    private var _binding: FragmentFeedHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FeedHomeViewModel by activityViewModels()
    private lateinit var sortStatus: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.BACKGROUND)
        sortStatus = "score"

        lifecycleScope.launch {
            launch { viewModel.getFeedList(null, 100, sortStatus) }
            launch { viewModel.getStudyCount() }
            launch { viewModel.getAlertCount(null, 1) }
        }
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
                    alarmActiveDot.visibility = if (it.isAlert) VISIBLE else INVISIBLE
                    feedCnt.text = if (it.studyCnt == null) "" else it.studyCnt.toString()

                    if (it.isFeedEmpty == null) {
                        loadingImg.visibility = VISIBLE
                    } else {
                        loadingImg.visibility = GONE
                        if (!it.isFeedEmpty!!) {
                            isNoStudyLayout.visibility = GONE
                        } else {
                            isNoStudyLayout.visibility = VISIBLE
                        }
                    }
                    updateStudyList(it.studyInfoList, it.studyCategoryMappingMap)
                    binding.enableStudyCheckBtn.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            updateStudyList(it.studyInfoList, it.studyCategoryMappingMap)
                        } else {
                            setFeedList(it.studyInfoList, it.studyCategoryMappingMap)
                        }
                    }
                }
            }
        }
        with(binding) {
            alarmBtn.setOnClickListener {
                val intent = Intent(requireContext(), MainHomeAlertActivity::class.java)
                startActivity(intent)
            }
            sortBtn.setOnClickListener {
                var standard: String
                if (sortStatus == "createdDateTime") {
                    standard = "score"
                    sortBtnText.text = "랭킹순"
                } else {
                    standard = "createdDateTime"
                    sortBtnText.text = "최신순"
                }
                sortStatus = standard
                viewLifecycleOwner.lifecycleScope.launch {
                    launch { viewModel.getFeedList(null, 100, sortStatus) }
                    launch { viewModel.getStudyCount() }
                    launch { viewModel.getAlertCount(null, 1) }
                }
            }
            feedSwipeRefreshLayout.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    launch { viewModel.getFeedList(null, 100, sortStatus) }
                    launch { viewModel.getStudyCount() }
                    feedSwipeRefreshLayout.isRefreshing = false
                }
            }
            makeNewStudyBtn.setOnClickListener {
                val intent = Intent(requireContext(), MakeStudyActivity::class.java)
                intent.putExtra("studyCnt", viewModel.uiState.value.studyCnt)
                startActivity(intent)
            }
        }
    }


    // 원래 페이지로 돌아왔을 때 state 업데이트
    override fun onResume() {
        super.onResume()
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.BACKGROUND)
        viewLifecycleOwner.lifecycleScope.launch {
            launch { viewModel.getFeedList(null, 100, sortStatus) }
            launch { viewModel.getStudyCount() }
        }
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
                intent.putExtra("studyStatus", studyList[position].studyInfo.status)
                startActivity(intent)
            }

            override fun bookmarkClick(view: View, position: Int) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.setBookmarkStatus(studyList[position].studyInfo.id)
                }
            }
        }
    }

    private fun updateStudyList(
        studyList: List<StudyInfoWithBookmarkStatus>,
        studyCategoryMappingMap: Map<Int, List<String>>
    ) {
        val activeStudyList = studyList.filter { it.studyInfo.status != StudyStatus.STUDY_INACTIVE && it.studyInfo.status != StudyStatus.STUDY_DELETED}
        setFeedList(activeStudyList, studyCategoryMappingMap)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}