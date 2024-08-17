package com.takseha.presentation.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentProfileHomeBinding
import com.takseha.presentation.viewmodel.profile.ProfileHomeViewModel
import com.takseha.presentation.viewmodel.profile.ProfileInfoUiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileHomeFragment : Fragment() {
    private var _binding: FragmentProfileHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileHomeViewModel by viewModels()

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getUserProfileInfo()
            viewModel.uiState.collectLatest {
                setUserInfo(it)
            }
        }

        with(binding) {
            editBtn.setOnClickListener {
                val intent = Intent(requireContext(), ProfileEditActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setUserInfo(
        userInfo: ProfileInfoUiState
    ) {
        with(binding) {
            nickname.text = userInfo.name
            githubIdText.text = getString(R.string.github_id, userInfo.githubId)
            Glide.with(this@ProfileHomeFragment)
                .load(userInfo.profileImgUrl)
                .error(R.drawable.logo_profile_default)
                .into(profileImg)
            githubLink.text = userInfo.socialInfo?.githubLink ?: "등록된 링크가 없어요"
            blogLink.text = userInfo.socialInfo?.blogLink ?: "등록된 링크가 없어요"
            linkedinLink.text = userInfo.socialInfo?.linkedInLink ?: "등록된 링크가 없어요"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}