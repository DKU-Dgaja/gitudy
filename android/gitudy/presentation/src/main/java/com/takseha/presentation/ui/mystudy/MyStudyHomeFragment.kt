package com.takseha.presentation.ui.mystudy

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
import com.takseha.presentation.adapter.MyStudyRVAdapter
import com.takseha.presentation.databinding.FragmentMyStudyHomeBinding
import com.takseha.presentation.viewmodel.home.MainHomeViewModel
import com.takseha.presentation.viewmodel.home.MyStudyWithTodo
import com.takseha.presentation.viewmodel.mystudy.MyStudyHomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// TODO: 활동 중인 스터디만 보기 기능 구현(studyStatus 보고 종료된 스터디 제외시키기)
class MyStudyHomeFragment : Fragment() {
    private var _binding: FragmentMyStudyHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyStudyHomeViewModel by activityViewModels()
    private lateinit var sortStatus: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.BACKGROUND)
        sortStatus = "score"

        lifecycleScope.launch {
            launch { viewModel.getMyStudyList(null, 50, sortStatus) }
            launch { viewModel.getStudyCount() }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyStudyHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.myStudyState.collectLatest {
                binding.myStudyCnt.text = it.studyCnt.toString()

                if (it.isMyStudiesEmpty == null) {
                    binding.loadingImg.visibility = VISIBLE
                } else {
                    binding.loadingImg.visibility = GONE
                    if (!it.isMyStudiesEmpty!!) {
                        binding.isNoStudyLayout.visibility = GONE
                    } else {
                        binding.isNoStudyLayout.visibility = VISIBLE
                    }
                    setMyStudyList(it.myStudiesWithTodo)
                }
            }
        }
        // 정렬: 최신순, 랭킹순
        with(binding) {
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
                    launch { viewModel.getMyStudyList(null, 50, sortStatus) }
                    launch { viewModel.getStudyCount() }
                }
            }
            myStudySwipeRefreshLayout.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    launch { viewModel.getMyStudyList(null, 50, sortStatus) }
                    launch { viewModel.getStudyCount() }
                    myStudySwipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    // 원래 페이지로 돌아왔을 때 state 업데이트
    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            launch { viewModel.getMyStudyList(null, 50, sortStatus) }
            launch { viewModel.getStudyCount() }
        }
    }

    private fun setMyStudyList(studyList: List<MyStudyWithTodo>) {
        with(binding) {
            val myStudyRVAdapter = MyStudyRVAdapter(requireContext(), studyList)

            myStudyList.adapter = myStudyRVAdapter
            myStudyList.layoutManager = LinearLayoutManager(requireContext())

            clickMyStudyItem(myStudyRVAdapter, studyList)
        }
    }

    private fun clickMyStudyItem(
        myStudyRVAdapter: MyStudyRVAdapter,
        studyList: List<MyStudyWithTodo>
    ) {
        myStudyRVAdapter.itemClick = object : MyStudyRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val intent = Intent(requireContext(), MyStudyMainActivity::class.java)
                intent.putExtra("studyInfoId", studyList[position].studyInfo.id)
                intent.putExtra("isLeader", studyList[position].studyInfo.isLeader)
                intent.putExtra("studyImgColor", studyList[position].studyInfo.profileImageUrl)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
