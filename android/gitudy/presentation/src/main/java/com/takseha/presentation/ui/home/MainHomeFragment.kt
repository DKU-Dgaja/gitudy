package com.takseha.presentation.ui.home

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentMainHomeBinding
import com.takseha.presentation.viewmodel.home.MainHomeUserInfoUiState
import com.takseha.presentation.viewmodel.home.MainHomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


// progress bar 끝 부분 둥글게 하는 건 추후 리팩토링 시 구현해보자..
class MainHomeFragment : Fragment() {
    private var _binding: FragmentMainHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainHomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor = Color.argb(0xFF,0x1B,0x1B,0x25)
    }

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

        val userInfo = arguments?.getSerializable("userInfo") as MainHomeUserInfoUiState
        setUserInfoInit(userInfo)

        val characterAnim = AnimationUtils.loadAnimation(context, R.anim.alpha_character)
        binding.characterImg.startAnimation(characterAnim)
    }

    private fun setMyStudyList() {
        // recyclerView 관련 기능 구현
    }

    private fun setUserInfoInit(
        userInfo: MainHomeUserInfoUiState
    ) {
        if (userInfo.name == "") {  // stateflow 초기값 처리 로직
            viewModel = ViewModelProvider(this)[MainHomeViewModel::class.java]
            viewModel.getUserInfo()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.uiState.collectLatest {
                    setUserInfo(it)
                    Log.d("MainHomeFragment", it.toString())
                }
            }
        } else {
            setUserInfo(userInfo)
        }
    }

    private fun setUserInfo(
        userInfo: MainHomeUserInfoUiState
    ) {
        val scoreAndRankText = getString(R.string.home_my_rank)

        with(binding) {
            nickname.text = userInfo.name
            scoreAndRank.text = String.format(scoreAndRankText, userInfo.score, 0)
            profileProgressBar.max = userInfo.progressMax
            profileProgressBar.progress = userInfo.progressScore
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}