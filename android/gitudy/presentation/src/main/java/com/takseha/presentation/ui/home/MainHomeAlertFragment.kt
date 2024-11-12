package com.takseha.presentation.ui.home

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
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.data.dto.home.Notice
import com.takseha.presentation.R
import com.takseha.presentation.adapter.NoticeListRVAdapter
import com.takseha.presentation.databinding.FragmentMainHomeAlertBinding
import com.takseha.presentation.ui.feed.StudyApplyActivity
import com.takseha.presentation.ui.mystudy.MyStudyMainActivity
import com.takseha.presentation.viewmodel.home.MainHomeAlertViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainHomeAlertFragment : Fragment() {
    private var _binding: FragmentMainHomeAlertBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainHomeAlertViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainHomeAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                if (it != null) {
                    if (it.isNotEmpty()) {
                        binding.isNoAlertLayout.visibility = GONE
                    } else {
                        binding.isNoAlertLayout.visibility = VISIBLE
                    }
                    setNoticeList(it)
                }
            }
        }

        with(binding) {
            alertSwipeRefreshLayout.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.getNoticeList(null, 50)
                    alertSwipeRefreshLayout.isRefreshing = false
                }
            }
            backBtn.setOnClickListener {
                requireActivity().finish()
            }
            deleteAllBtn.setOnClickListener {
                viewModel.deleteAllNotice(null, 50)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.WHITE)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getNoticeList(null, 50)
        }
    }

    private fun setNoticeList(noticeList: List<Notice>) {
        with(binding) {
            val noticeListRVAdapter = NoticeListRVAdapter(requireContext(), noticeList)
            alertList.adapter = noticeListRVAdapter
            alertList.layoutManager = LinearLayoutManager(requireContext())

            val itemTouchHelperCallback = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.bindingAdapterPosition
                    val noticeId = noticeList[position].id
                    viewModel.deleteNotice(noticeId, null, 50)
                }
            }

            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(alertList)

            clickNoticeItem(noticeListRVAdapter, noticeList)
        }
    }

    // TODO: 추후 수정 필요
    private fun clickNoticeItem(
        noticeListRVAdapter: NoticeListRVAdapter,
        noticeList: List<Notice>
    ) {
        noticeListRVAdapter.onClickListener = object : NoticeListRVAdapter.OnClickListener {
            override fun onClick(view: View, position: Int) {
                val notice = noticeList[position]

                if (notice.title.contains("스터디 가입 신청")) {
                    val bundle = Bundle().apply {
                        putInt("studyInfoId", notice.studyInfoId)
                    }
                    view.findNavController().navigate(
                        R.id.action_mainHomeAlertFragment_to_studyApplyMemberListFragment,
                        bundle
                    )
                } else if (notice.title.contains("실패")) {
                    val intent = Intent(requireContext(), StudyApplyActivity::class.java).apply {
                        putExtra("studyInfoId", notice.studyInfoId)
                        putExtra("studyStatus", StudyStatus.STUDY_PUBLIC)
                    }
                    startActivity(intent)
                } else if (notice.title.contains("탈퇴")) {

                } else if (notice.title.contains("스터디")) {
                    val intent = Intent(requireContext(), MyStudyMainActivity::class.java).apply {
                        putExtra("studyInfoId", notice.studyInfoId)
                        putExtra("studyStatus", StudyStatus.STUDY_PUBLIC)
                    }
                    startActivity(intent)
                } else if (notice.title.contains("TO-DO 업데이트")) {
                    val intent = Intent(requireContext(), MyStudyMainActivity::class.java).apply {
                        putExtra("studyInfoId", notice.studyInfoId)
                        putExtra("targetFragment", "toDoFragment")
                        putExtra("studyStatus", StudyStatus.STUDY_PUBLIC)
                    }
                    startActivity(intent)
                } else if (notice.title.contains("커밋")) {
                    val intent = Intent(requireContext(), MyStudyMainActivity::class.java).apply {
                        putExtra("studyInfoId", notice.studyInfoId)
                        putExtra("targetFragment", "toDoFragment")
                        putExtra("studyStatus", StudyStatus.STUDY_PUBLIC)
                    }
                    startActivity(intent)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}