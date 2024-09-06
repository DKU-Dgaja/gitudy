package com.takseha.presentation.ui.mystudy

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.takseha.data.dto.mystudy.StudyApplyMember
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentStudyApplyMemberProfileBinding
import com.takseha.presentation.ui.common.CustomSetDialog
import com.takseha.presentation.viewmodel.mystudy.StudyApplyMemberProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StudyApplyMemberProfileFragment : Fragment() {
    private var _binding: FragmentStudyApplyMemberProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudyApplyMemberProfileViewModel by activityViewModels()
    private var studyInfoId: Int = 0
    private var profileInfo: StudyApplyMember? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.WHITE)
        studyInfoId = arguments?.getInt("studyInfoId") ?: 0
        profileInfo = arguments?.getSerializable("memberProfile") as StudyApplyMember
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
        setMemberProfile(profileInfo)
        with(binding) {
            exitBtn.setOnClickListener {
                it.findNavController().popBackStack()
            }
            applyBtn.setOnClickListener {
                showApproveMemberDialog(studyInfoId, profileInfo?.userId ?: 0, true, getString(R.string.study_approve_member_apply))
            }
            cancelBtn.setOnClickListener {
                showApproveMemberDialog(studyInfoId, profileInfo?.userId ?: 0, false, getString(R.string.study_withdraw_member_apply))
            }
        }
    }

    private fun setMemberProfile(profileInfo: StudyApplyMember?) {
        with(binding) {
            Glide.with(requireContext())
                .load(profileInfo?.profileImageUrl)
                .error(R.drawable.logo_profile_default)
                .into(profileImg)

            nickname.text = profileInfo?.name
            githubId.text = getString(R.string.github_id, profileInfo?.githubId)
            githubLink.text = if (profileInfo?.socialInfo?.githubLink == null || profileInfo.socialInfo?.githubLink == "") {
                "링크 미등록"
            } else {
                profileInfo.socialInfo!!.githubLink
            }
            blogLink.text = if (profileInfo?.socialInfo?.blogLink == null || profileInfo.socialInfo?.blogLink == "") {
                "링크 미등록"
            } else {
                profileInfo.socialInfo!!.blogLink
            }
            linkedInLink.text = if (profileInfo?.socialInfo?.linkedInLink== null || profileInfo.socialInfo?.linkedInLink == "") {
                "링크 미등록"
            } else {
                profileInfo.socialInfo!!.linkedInLink
            }
            if (profileInfo?.signGreeting == null || profileInfo.signGreeting == "") {
                messageContent.text = "메세지가 없어요"
                messageContent.setTextColor(ContextCompat.getColor(requireContext(), R.color.GS_500))
            } else {
                messageContent.text = profileInfo.signGreeting
            }

            githubLinkBtn.setOnClickListener {
                val textToCopy = profileInfo?.socialInfo?.githubLink ?: ""
                val clipboard =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("githubLink", textToCopy)
                clipboard.setPrimaryClip(clip)
            }
            blogLinkBtn.setOnClickListener {
                val textToCopy = profileInfo?.socialInfo?.blogLink ?: ""
                val clipboard =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("blogLink", textToCopy)
                clipboard.setPrimaryClip(clip)
            }
            linkedinLinkBtn.setOnClickListener {
                val textToCopy = profileInfo?.socialInfo?.linkedInLink ?: ""
                val clipboard =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("linkedinLink", textToCopy)
                clipboard.setPrimaryClip(clip)
            }
        }
    }

    private fun showApproveMemberDialog(studyInfoId: Int, applyUserId: Int, approve: Boolean, alertMessage: String) {
        val customSetDialog = CustomSetDialog(requireContext())
        customSetDialog.setAlertText(alertMessage)
        customSetDialog.setOnConfirmClickListener {
            with(binding) {
                loadingIndicator.visibility = VISIBLE
                exitBtn.isEnabled = false
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
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.resetResponseState()
                viewModel.approveApplyMember(studyInfoId, applyUserId, approve)
                viewModel.responseState.collectLatest {
                    if (it != null) {
                        with(binding) {
                            loadingIndicator.visibility = GONE
                            exitBtn.isEnabled = true
                        }
                        if (it) {
                            with(binding) {
                                if (approve) {
                                    applyBtn.text = "수락 완료"
                                } else {
                                    cancelBtn.text = "거절 완료"
                                }
                            }
                        } else {
                            with(binding) {
                                if (approve) {
                                    applyBtn.text = "수락 실패"
                                } else {
                                    cancelBtn.text = "거절 실패"
                                }
                            }
                        }

                    }
                }
            }
        }
        customSetDialog.show()
    }
}