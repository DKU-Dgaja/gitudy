package com.takseha.presentation.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.data.dto.profile.Bookmark
import com.takseha.data.dto.profile.CommitWithStudyName
import com.takseha.presentation.R
import com.takseha.presentation.adapter.BookmarkListRVAdapter
import com.takseha.presentation.adapter.MyCommitListRVAdapter
import com.takseha.presentation.databinding.FragmentProfileHomeBinding
import com.takseha.presentation.ui.feed.StudyApplyActivity
import com.takseha.presentation.ui.home.MainHomeAlertActivity
import com.takseha.presentation.viewmodel.profile.ProfileHomeViewModel
import com.takseha.presentation.viewmodel.profile.ProfileInfoUiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileHomeFragment : Fragment() {
    private var _binding: FragmentProfileHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileHomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.BACKGROUND)
        lifecycleScope.launch {
            launch { viewModel.getUserProfileInfo() }
            launch { viewModel.getUserInfo() }
            launch { viewModel.getBookmarks(null, 3) }
            launch { viewModel.getMyCommitLists(null, null, 3) }
        }
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
            viewModel.userUiState.collectLatest { userUiState ->
                setUserInfo(userUiState)
                with(binding) {
                    settingBtn.setOnClickListener {
                        val intent = Intent(requireContext(), SettingActivity::class.java)
                        intent.putExtra("pushAlarmYn", userUiState.pushAlarmYn)
                        startActivity(intent)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.bookmarksState.collectLatest { bookmarksState ->
                if (!bookmarksState.isBookmarksEmpty) {
                    binding.isNoBookmarkLayout.visibility = GONE
                } else {
                    binding.isNoBookmarkLayout.visibility = VISIBLE
                }
                setBookmarkList(bookmarksState.bookmarksInfo)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.myCommitsState.collectLatest { myCommitsState ->
                if (!myCommitsState.isMyCommitEmpty) {
                    binding.isNoCommit.visibility = GONE
                } else {
                    binding.isNoCommit.visibility = VISIBLE
                }
                setMyCommitList(myCommitsState.commitList)
            }
        }

        with(binding) {
            editBtn.setOnClickListener {
                val intent = Intent(requireContext(), ProfileEditActivity::class.java)
                startActivity(intent)
            }
            commitMoreBtn.setOnClickListener {
                val intent = Intent(requireContext(), MyCommitActivity::class.java)
                intent.putExtra("isLeader", false)
                intent.putExtra("studyStatus", StudyStatus.STUDY_INACTIVE)
                startActivity(intent)
            }
            bookmarkMoreBtn.setOnClickListener {
                val intent = Intent(requireContext(), BookmarksActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            launch { viewModel.getUserProfileInfo() }
            launch { viewModel.getUserInfo() }
            launch { viewModel.getBookmarks(null, 3) }
            launch { viewModel.getMyCommitLists(null, null, 3) }
        }
    }

    private fun setUserInfo(
        userInfo: ProfileInfoUiState
    ) {
        with(binding) {
            nickname.text = userInfo.name
            githubIdText.text = getString(R.string.github_id, userInfo.githubId)
            Glide.with(this@ProfileHomeFragment)
                .load(userInfo.profileImageUrl)
                .error(R.drawable.logo_profile_default)
                .into(profileImg)
            githubLink.text = if (userInfo.socialInfo?.githubLink == null || userInfo.socialInfo.githubLink == "") {
                "등록된 링크가 없어요"
            } else {
                userInfo.socialInfo.githubLink
            }
            blogLink.text = if (userInfo.socialInfo?.blogLink == null || userInfo.socialInfo.blogLink == "") {
                "등록된 링크가 없어요"
            } else {
                userInfo.socialInfo.blogLink
            }
            linkedinLink.text = if (userInfo.socialInfo?.linkedInLink == null || userInfo.socialInfo.linkedInLink == "") {
                "등록된 링크가 없어요"
            } else {
                userInfo.socialInfo.linkedInLink
            }
        }
    }

    private fun setBookmarkList(bookmarks: List<Bookmark>) {
        with(binding) {
            val bookmarkListRVAdapter = BookmarkListRVAdapter(requireContext(), bookmarks)

            bookmarkList.adapter = bookmarkListRVAdapter
            bookmarkList.layoutManager = LinearLayoutManager(requireContext())

            clickBookmarkItem(bookmarkListRVAdapter, bookmarks)
        }
    }

    private fun clickBookmarkItem(
        bookmarkListRVAdapter: BookmarkListRVAdapter,
        bookmarks: List<Bookmark>
    ) {
        bookmarkListRVAdapter.onClickListener = object : BookmarkListRVAdapter.OnClickListener {
            override fun onClick(view: View, position: Int) {
                val intent = Intent(requireContext(), StudyApplyActivity::class.java)
                intent.putExtra("studyInfoId", bookmarks[position].studyInfoId)
                intent.putExtra(
                    "studyImgColor",
                    bookmarks[position].studyInfoWithIdResponse.profileImageUrl
                )
                intent.putExtra("studyStatus", bookmarks[position].studyInfoWithIdResponse.status)
                startActivity(intent)
            }

            override fun bookmarkClick(view: View, position: Int) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.setBookmarkStatus(bookmarks[position].studyInfoId, null, 3)
                }
            }
        }
    }

    private fun setMyCommitList(commits: List<CommitWithStudyName>) {
        with(binding) {
            val myCommitListRVAdapter = MyCommitListRVAdapter(requireContext(), commits)

            commitList.adapter = myCommitListRVAdapter
            commitList.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}