package com.takseha.presentation.ui.mystudy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.takseha.data.dto.mystudy.StudyApplyMember
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentStudyApplyMemberProfileBinding
import com.takseha.presentation.ui.common.CustomDialog
import com.takseha.presentation.viewmodel.mystudy.StudyApplyMemberProfileViewModel

class StudyApplyMemberProfileFragment : Fragment() {
    private var _binding: FragmentStudyApplyMemberProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudyApplyMemberProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.BACKGROUND_BLACK)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudyApplyMemberProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val studyInfoId = arguments?.getInt("studyInfoId") ?: 0
        val profileInfo = arguments?.getSerializable("memberProfile") as StudyApplyMember
        setMemberProfile(profileInfo)
        with(binding) {
            exitBtn.setOnClickListener {
                it.findNavController().popBackStack()
            }
            applyBtn.setOnClickListener {
                showApproveMemberDialog(studyInfoId, profileInfo.userId, true, getString(R.string.study_approve_member_apply))
            }
            cancelBtn.setOnClickListener {
                showApproveMemberDialog(studyInfoId, profileInfo.userId, false, getString(R.string.study_withdraw_member_apply))
            }
        }
    }

    private fun setMemberProfile(profileInfo: StudyApplyMember) {
        with(binding) {
            Glide.with(requireContext())
                .load(profileInfo.profileImageUrl)
                .error(R.drawable.logo_profile_default)
                .into(profileImg)

            nickname.text = profileInfo.name
            githubId.text = getString(R.string.github_id, profileInfo.githubId)
            githubLink.text = profileInfo.socialInfo?.githubLink ?: "링크 미등록"
            blogLink.text = profileInfo.socialInfo?.blogLink ?: "링크 미등록"
            linkedInLink.text = profileInfo.socialInfo?.linkedInLink ?: "링크 미등록"
            messageContent.text = profileInfo.signGreeting
        }
    }

    private fun showApproveMemberDialog(studyInfoId: Int, applyUserId: Int, approve: Boolean, alertMessage: String) {
        val customDialog = CustomDialog(requireContext())
        customDialog.setAlertText(alertMessage)
        customDialog.setOnConfirmClickListener {
            viewModel.approveApplyMember(studyInfoId, applyUserId, approve)
            with(binding) {
                if (approve) {
                    applyBtn.text = "수락 완료"
                } else {
                    cancelBtn.text = "거절 완료"
                }
                applyBtn.apply {
                    isEnabled = false
                    backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.GS_200)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.GS_400))
                }
                cancelBtn.apply {
                    isEnabled = false
                    backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.GS_200)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.GS_400))
                }
            }
        }
        customDialog.show()
    }
}