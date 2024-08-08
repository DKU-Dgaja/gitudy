package com.takseha.presentation.ui.mystudy

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
import com.takseha.presentation.R
import com.takseha.presentation.adapter.MyStudyRVAdapter
import com.takseha.presentation.databinding.FragmentMyStudyHomeBinding
import com.takseha.presentation.ui.feed.MakeStudyActivity
import com.takseha.presentation.viewmodel.home.MainHomeViewModel
import com.takseha.presentation.viewmodel.home.MyStudyWithTodo
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// TODO: 정렬 버튼 선택 시 정렬 기준 변경, 활동 중인 스터디만 보기 기능 구현(studyStatus 보고 종료된 스터디 제외시키기)
class MyStudyHomeFragment : Fragment() {
    private var _binding: FragmentMyStudyHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainHomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.BACKGROUND)
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
        with(binding) {
            makeNewStudyBtn.setOnClickListener {
                startActivity(Intent(activity, MakeStudyActivity::class.java))
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.myStudyState.collectLatest {
                binding.myStudyCnt.text = it.studyCnt.toString()
                if (!it.isMyStudiesEmpty) {
                    binding.isNoStudyLayout.visibility = View.GONE
                    setMyStudyList(it.myStudiesWithTodo)
                } else {
                    binding.isNoStudyLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    // 원래 페이지로 돌아왔을 때 state 업데이트
    override fun onResume() {
        super.onResume()
        viewModel.getMyStudyList(null, 7)
    }

    private fun setMyStudyList(studyList: List<MyStudyWithTodo>) {
        with(binding) {
            val myStudyRVAdapter = MyStudyRVAdapter(requireContext(), studyList)

            myStudyList.adapter = myStudyRVAdapter
            myStudyList.layoutManager = LinearLayoutManager(requireContext())

            clickMyStudyItem(myStudyRVAdapter, studyList)
        }
    }

    private fun clickMyStudyItem(myStudyRVAdapter: MyStudyRVAdapter, studyList: List<MyStudyWithTodo>) {
        myStudyRVAdapter.itemClick = object : MyStudyRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val intent = Intent(requireContext(), MyStudyMainActivity::class.java)
                intent.putExtra("studyInfoId", studyList[position].studyInfo.id)
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
