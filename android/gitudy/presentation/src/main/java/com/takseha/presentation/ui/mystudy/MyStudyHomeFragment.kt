package com.takseha.presentation.ui.mystudy

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
import com.takseha.data.dto.mystudy.MyStudyWithTodo
import com.takseha.presentation.R
import com.takseha.presentation.adapter.MyStudyRVAdapter
import com.takseha.presentation.databinding.FragmentMyStudyHomeBinding
import com.takseha.presentation.viewmodel.home.MainHomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.myStudyState.collectLatest {
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
                intent.putExtra("studyImgColor", studyList[position].studyImg)
                Log.d("MyStudyHomeFragment", intent.extras.toString())
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
