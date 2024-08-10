package com.takseha.presentation.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.presentation.R
import com.takseha.presentation.adapter.MyStudyRVAdapter
import com.takseha.presentation.databinding.FragmentMainHomeBinding
import com.takseha.presentation.ui.mystudy.MyStudyMainActivity
import com.takseha.presentation.viewmodel.home.MainHomeUserInfoUiState
import com.takseha.presentation.viewmodel.home.MainHomeViewModel
import com.takseha.presentation.viewmodel.home.MyStudyWithTodo
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


// TODO: progress bar 끝 부분 둥글게 하는 건 추후 리팩토링 시 구현해보자..
// TODO: 홈 화면에 마이스터디 목록 말고 다른 걸 넣는 게 더 좋을 거 같다!! 생각해보기
class MainHomeFragment : Fragment() {
    private var _binding: FragmentMainHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainHomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor = Color.argb(0xFF,0x1B,0x1B,0x25)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainHomeBinding.inflate(inflater, container, false)

        setViewModel()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.apply {
                uiState.collectLatest {
                    setUserInfo(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.apply {
                myStudyState.collectLatest {
                    if (!it.isMyStudiesEmpty) {
                        binding.isNoStudyLayout.visibility = GONE
                        setMyStudyList(it.myStudiesWithTodo)
                    } else {
                        binding.isNoStudyLayout.visibility = VISIBLE
                    }
                }
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

    private fun setViewModel() {
        lifecycleScope.launch {
            viewModel.apply {
                getUserInfo()
                getMyStudyList(null, 7)
            }
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
        }
    }

    private fun clickMyStudyItem(myStudyRVAdapter: MyStudyRVAdapter, studyList: List<MyStudyWithTodo>) {
        myStudyRVAdapter.itemClick = object : MyStudyRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val intent = Intent(requireContext(), MyStudyMainActivity::class.java)
                intent.putExtra("studyInfoId", studyList[position].studyInfo.id)
                intent.putExtra("studyImgColor", studyList[position].studyInfo.profileImageUrl)
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