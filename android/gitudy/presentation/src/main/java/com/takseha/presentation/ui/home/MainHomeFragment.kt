package com.takseha.presentation.ui.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentMainHomeBinding
import com.takseha.presentation.viewmodel.MainHomeViewModel
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MainHomeViewModel::class.java]

        collectUserInfoFlows()
        setMyStudyList()

        val characterAnim = AnimationUtils.loadAnimation(context, R.anim.alpha_character)
        binding.characterImg.startAnimation(characterAnim)


    }

    private fun collectUserInfoFlows() {
        viewModel.getUserInfo()
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(
                lifecycle = viewLifecycleOwner.lifecycle,
                minActiveState = Lifecycle.State.STARTED
            ).collect {
                setUserInfo(it.name, it.score, it.progressScore, it.progressMax)
            }
        }
    }

    private fun setMyStudyList() {
        // recyclerView 관련 기능 구현
    }

    private fun setUserInfo(
        name: String,
        score: Int,
        progressScore: Int,
        progressMax: Int
    ) {
        val scoreAndRankText = getString(R.string.home_my_rank)

        with(binding) {
            nickname.text = name
            scoreAndRank.text = String.format(scoreAndRankText, score, 0)
            profileProgressBar.max = progressMax
            profileProgressBar.progress = progressScore
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}