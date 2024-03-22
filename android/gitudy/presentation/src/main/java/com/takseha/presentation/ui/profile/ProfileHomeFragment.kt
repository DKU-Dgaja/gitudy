package com.takseha.presentation.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentProfileHomeBinding
import com.takseha.presentation.viewmodel.ProfileHomeViewModel
import kotlinx.coroutines.launch

class ProfileHomeFragment : Fragment() {
    private var _binding: FragmentProfileHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileHomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.BACKGROUND)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ProfileHomeViewModel::class.java]

        collectUserInfoFlows()

    }

    private fun collectUserInfoFlows() {
        viewModel.getUserInfo()
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(
                lifecycle = viewLifecycleOwner.lifecycle,
                minActiveState = Lifecycle.State.STARTED
            ).collect {
                setUserInfo(it.name, it.githubId, it.profileImgUrl)
            }
        }
    }

    private fun setUserInfo(
        name: String,
        githubId: String,
        profileImgUrl: String
    ) {
        with(binding) {
            nickname.text = name
            githubIdText.text = "@$githubId"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}