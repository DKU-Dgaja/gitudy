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
import com.takseha.data.dto.home.Notice
import com.takseha.presentation.R
import com.takseha.presentation.adapter.NoticeListRVAdapter
import com.takseha.presentation.databinding.FragmentMainHomeAlertBinding
import com.takseha.presentation.ui.mystudy.MyStudyMainActivity
import com.takseha.presentation.viewmodel.home.MainHomeAlertViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainHomeAlertFragment : Fragment() {
    private var _binding: FragmentMainHomeAlertBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainHomeAlertViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.WHITE)
    }

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
                    }
                    else {
                        binding.isNoAlertLayout.visibility = VISIBLE
                    }
                    setNoticeList(it)
                }
            }
        }

        with(binding) {
            backBtn.setOnClickListener {
                requireActivity().finish()
            }
            deleteAllBtn.setOnClickListener {
                viewModel.deleteAllNotice(null, 50)
            }
        }
    }

    // 원래 페이지로 돌아왔을 때 state 업데이트
    override fun onResume() {
        super.onResume()
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.WHITE)
        viewModel.getNoticeList(null, 50)
    }

    private fun setNoticeList(noticeList: List<Notice>) {
        with(binding) {
            val noticeListRVAdapter = NoticeListRVAdapter(requireContext(), noticeList)
            alertList.adapter = noticeListRVAdapter
            alertList.layoutManager = LinearLayoutManager(requireContext())

            // ItemTouchHelper 설정. 스와이프하여 알림 삭제
            val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.getBindingAdapterPosition()
                    val noticeId = noticeList[position].id
                    viewModel.deleteNotice(noticeId, null, 50)
                }
            }

            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(alertList)

            clickNoticeItem(noticeListRVAdapter, noticeList)
        }
    }

    private fun clickNoticeItem(noticeListRVAdapter: NoticeListRVAdapter, noticeList: List<Notice>) {
        noticeListRVAdapter.onClickListener = object : NoticeListRVAdapter.OnClickListener {
            override fun onClick(view: View, position: Int) {
                val notice = noticeList[position]

                if (notice.title.contains("스터디 가입 신청")) {   // 스터디 가입 신청
                    val bundle = Bundle().apply {
                        putInt("studyInfoId", notice.studyInfoId)
                    }
                    view.findNavController().navigate(R.id.action_mainHomeAlertFragment_to_studyApplyMemberListFragment, bundle)
                } else if (notice.title.contains("스터디 가입 완료")) {    // 스터디 가입 완료
                    // 해당 스터디 상세 페이지로 이동
                    val intent = Intent(requireContext(), MyStudyMainActivity::class.java)
                    intent.putExtra("studyInfoId", notice.studyInfoId)
                    startActivity(intent)
                } else if (notice.title.contains("TO-DO 업데이트")) {  // 스터디 TO-DO 업데이트
                    // 해당 스터디 TO-DO 상세 페이지로 이동
                    val bundle = Bundle().apply {
                        putInt("studyInfoId", notice.studyInfoId)
                    }
                    view.findNavController().navigate(R.id.action_mainHomeAlertFragment_to_toDoFragment, bundle)
                } else if (notice.title.contains("커밋 승인") || notice.title.contains("커밋 반려")) {
                    // 해당 스터디 TO-DO 상세 페이지로 이동
                    val bundle = Bundle().apply {
                        putInt("studyInfoId", notice.studyInfoId)
                    }
                    view.findNavController().navigate(R.id.action_mainHomeAlertFragment_to_toDoFragment, bundle)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}