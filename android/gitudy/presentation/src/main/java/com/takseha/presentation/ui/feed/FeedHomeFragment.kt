package com.takseha.presentation.ui.feed

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.presentation.R
import com.takseha.presentation.adapter.FeedRVAdapter
import com.takseha.presentation.databinding.FragmentFeedHomeBinding
import com.takseha.presentation.viewmodel.feed.FeedHomeViewModel

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

            viewModel.getFeedList(null, 5, "createdDateTime")
            viewModel.feedStudyInfo.observe(viewLifecycleOwner) {
                val feedRVAdapter = FeedRVAdapter(requireContext(), it)

                if (feedRVAdapter.itemCount > 0) {
                    isNoStudyLayout.visibility = View.GONE
                }

                feedList.adapter = feedRVAdapter
                feedList.layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}