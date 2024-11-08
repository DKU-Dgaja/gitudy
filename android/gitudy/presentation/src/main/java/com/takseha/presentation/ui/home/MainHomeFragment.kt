package com.takseha.presentation.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.mystudy.TodoStatus
import com.takseha.presentation.R
import com.takseha.presentation.adapter.MainStudyRVAdapter
import com.takseha.presentation.databinding.FragmentMainHomeBinding
import com.takseha.presentation.ui.mystudy.MyStudyMainActivity
import com.takseha.presentation.viewmodel.home.MainHomeUserInfoUiState
import com.takseha.presentation.viewmodel.home.MainHomeViewModel
import com.takseha.presentation.viewmodel.home.MyStudyWithTodo
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


// TODO: progress bar 끝 부분 둥글게 하는 건 추후 리팩토링 시 구현해보자..
class MainHomeFragment : Fragment() {
    private var _binding: FragmentMainHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainHomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                setUserInfo(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.myStudyState.collectLatest {
                if (!it.isMyStudiesEmpty) {
                    binding.isNoStudyLayout.visibility = GONE
                } else {
                    binding.isNoStudyLayout.visibility = VISIBLE
                }
                setMyStudyList(it.myStudiesWithTodo)
            }
        }

        with(binding) {
            val characterAnim = AnimationUtils.loadAnimation(context, R.anim.alpha_character)
            characterImg.startAnimation(characterAnim)
            alarmBtn.setOnClickListener {
                val intent = Intent(requireContext(), MainHomeAlertActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // 원래 페이지로 돌아왔을 때 state 업데이트
    override fun onResume() {
        super.onResume()
        requireActivity().window.statusBarColor = Color.argb(0xFF, 0x1B, 0x1B, 0x25)
        viewLifecycleOwner.lifecycleScope.launch {
            launch { viewModel.getUserInfo() }
            launch { viewModel.getMyStudyList(null, 50, "score") }
            launch { viewModel.getAlertCount(null, 1) }
        }
    }

    private fun setMyStudyList(studyList: List<MyStudyWithTodo>) {
        val uncompletedStudyList = studyList.filter { study ->
            study.urgentTodo?.myStatus != TodoStatus.TODO_COMPLETE && study.urgentTodo?.todo != null
        }

        with(binding) {
            if (uncompletedStudyList.isEmpty()) isNoStudyLayout.visibility = VISIBLE else isNoStudyLayout.visibility = GONE
            val mainStudyRVAdapter = MainStudyRVAdapter(requireContext(), uncompletedStudyList)

            myStudyList.adapter = mainStudyRVAdapter
            myStudyList.layoutManager = LinearLayoutManager(requireContext())

            clickMyStudyItem(mainStudyRVAdapter, uncompletedStudyList)
        }
    }


    private fun setUserInfo(
        userInfo: MainHomeUserInfoUiState
    ) {
        val scoreAndRankText = getString(R.string.home_my_rank)

        with(binding) {
            nickname.text = userInfo.name
            scoreAndRank.text = String.format(scoreAndRankText, userInfo.score, userInfo.rank)
            profileProgressBar.max = userInfo.progressMax
            profileProgressBar.progress = userInfo.progressScore
            characterImg.setImageResource(userInfo.characterImgSrc)
            alarmActiveDot.visibility = if (userInfo.isAlert) VISIBLE else INVISIBLE
        }
    }

    private fun clickMyStudyItem(
        mainStudyRVAdapter: MainStudyRVAdapter,
        studyList: List<MyStudyWithTodo>
    ) {
        mainStudyRVAdapter.itemClick = object : MainStudyRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val intent = Intent(requireContext(), MyStudyMainActivity::class.java)
                intent.putExtra("studyInfoId", studyList[position].studyInfo.id)
                intent.putExtra("isLeader", studyList[position].studyInfo.isLeader)
                intent.putExtra("studyImgColor", studyList[position].studyInfo.profileImageUrl)
                intent.putExtra("studyStatus", studyList[position].studyInfo.status)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}