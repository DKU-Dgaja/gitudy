package com.takseha.presentation.ui.mystudy

import android.os.Bundle
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

        viewModel.getMyStudyList(null, 10)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                    setMyStudyList(it.myStudiesWithTodo)
            }
        }
    }

    private fun setMyStudyList(studyList: List<MyStudyWithTodo>) {
        with(binding) {
            val myStudyRVAdapter = MyStudyRVAdapter(requireContext(), studyList)

            if (myStudyRVAdapter.itemCount > 0) {
                isNoStudyLayout.visibility = android.view.View.GONE
            }

            myStudyList.adapter = myStudyRVAdapter
            myStudyList.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
