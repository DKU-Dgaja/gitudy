package com.takseha.presentation.ui.profile

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.data.dto.mystudy.Comment
import com.takseha.data.dto.mystudy.Commit
import com.takseha.data.dto.mystudy.CommitStatus
import com.takseha.presentation.R
import com.takseha.presentation.adapter.CommentListRVAdapter
import com.takseha.presentation.adapter.CommitCommentListRVAdapter
import com.takseha.presentation.adapter.DetailCommentListRVAdapter
import com.takseha.presentation.databinding.FragmentCommitDetailBinding
import com.takseha.presentation.databinding.LayoutSnackbarGreyBinding
import com.takseha.presentation.databinding.LayoutSnackbarRedBinding
import com.takseha.presentation.ui.common.CustomSetDialog
import com.takseha.presentation.ui.common.KeyboardUtils
import com.takseha.presentation.viewmodel.mystudy.CommitDetailViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CommitDetailFragment : Fragment() {
    private var _binding : FragmentCommitDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CommitDetailViewModel by activityViewModels()
    private var isLeader: Boolean? = null
    private var studyStatus: StudyStatus? = null
    private var commit: Commit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isLeader = requireActivity().intent.getBooleanExtra("isLeader", false)
        studyStatus = requireActivity().intent.getSerializableExtra("studyStatus") as StudyStatus
        arguments?.let {
            commit = it.getSerializable("commit") as Commit?
        }
        lifecycleScope.launch {
            launch { viewModel.getRepositoryInfo(commit?.studyInfoId ?: 0) }
            launch { viewModel.getCommitComments(commit?.id ?: 0, commit?.studyInfoId ?: 0) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommitDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.WHITE
        )
        var comment = ""
        setupUI(view)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.repositoryInfoState.collectLatest { repositoryInfo ->
                with(binding) {
                    if (studyStatus == StudyStatus.STUDY_INACTIVE) messageLayout.visibility = GONE

                    if (isLeader!! && studyStatus != StudyStatus.STUDY_INACTIVE) {
                        if (commit?.status != CommitStatus.COMMIT_APPROVAL && commit?.status != CommitStatus.COMMIT_REJECTION) {
                            commitManageBtn.visibility = VISIBLE
                        }
                    } else {
                        commitManageBtn.visibility = GONE
                    }

                    commitTitle.text = commit?.message
                    commitInfo.text = getString(R.string.study_to_do_commit_info, commit?.name, commit?.commitDate,)
                    when (commit?.status) {
                        CommitStatus.COMMIT_APPROVAL -> {
                            commitStatus.text = "승인완료"
                            commitStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.BASIC_BLUE))
                        }
                        CommitStatus.COMMIT_DELETE -> {
                            commitStatus.text = "커밋삭제"
                            commitStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.GS_500))
                        }
                        CommitStatus.COMMIT_REJECTION -> {
                            commitStatus.text = "커밋반려"
                            commitStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.BASIC_RED))
                        }
                        CommitStatus.COMMIT_WAITING -> {
                            commitStatus.text = "승인대기"
                            commitStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.BASIC_GREEN))
                        }
                        null -> commitStatus.text = "승인완료"
                    }
                    Glide.with(this@CommitDetailFragment)
                        .load(commit?.profileImageUrl)
                        .error(R.drawable.logo_profile_default)
                        .into(profileImg)
                    nickname.text = commit?.name
                    githubLinkBtn.setOnClickListener {
                        val githubUrl = getString(R.string.commit_github_url, repositoryInfo.owner, repositoryInfo.name, commit?.commitSha)
                        val bundle = Bundle().apply {
                            putString("githubUrl", githubUrl)
                        }
                        Log.d("CommitDetailFragment", bundle.toString())
                        it.findNavController().navigate(R.id.action_commitDetailFragment_to_commitWebViewFragment, bundle)
                    }
                    backBtn.setOnClickListener {
                        it.findNavController().popBackStack()
                    }
                    commitManageBtn.setOnClickListener {
                        showCommitManageDialog(commit?.studyInfoId ?: 0, commit!!.id)
                    }
                    thumbBtn.setOnClickListener {
                        shakeBtn(it)
                    }
                    heartBtn.setOnClickListener {
                        shakeBtn(it)
                    }

                    newCommentBody.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            comment = newCommentBody.text.toString()
                            postBtn.isEnabled = comment.isNotEmpty()
                        }
                    })
                    postBtn.setOnClickListener {
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewModel.makeCommitComment(commit?.id ?: 0, commit?.studyInfoId ?: 0, comment)
                            Log.d("CommitDetailFragment", "${commit?.id}, ${commit?.studyInfoId ?: 0}")
                            newCommentBody.setText("")
                            val imm =
                                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(newCommentBody.windowToken, 0)
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.commentState.collectLatest {
                if (it != null) {
                    if (it.isEmpty()) {
                        binding.isNoCommentLayout.visibility = VISIBLE
                    } else {
                        binding.isNoCommentLayout.visibility = GONE
                    }
                    setCommitComments(it)
                }
            }
        }
    }

    private fun showCommitManageDialog(studyInfoId: Int, commitId: Int) {
        val customSetDialog = CustomSetDialog(requireContext())
        customSetDialog.setAlertText(getString(R.string.commit_approve))
        customSetDialog.setConfirmBtnText("승인")
        customSetDialog.setCancelBtnText("반려")
        customSetDialog.setCancelBtnTextColor(ContextCompat.getColor(requireContext(), R.color.BASIC_RED))
        customSetDialog.setOnConfirmClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.approveCommit(studyInfoId, commitId)
                with(binding) {
                    commitManageBtn.visibility = GONE
                    commitStatus.apply {
                        text = "승인완료"
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.BASIC_BLUE))
                    }
                }
            }
        }
        customSetDialog.setOnCancelClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.rejectCommit(studyInfoId, "거절 이유", commitId)
                with(binding) {
                    commitManageBtn.visibility = GONE
                    commitStatus.apply {
                        text = "커밋반려"
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.BASIC_RED))
                    }
                }
            }
        }
        customSetDialog.show()
    }

    private fun shakeBtn(view: View) {
        val translateX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -10f, 10f, -8f, 8f, -6f, 6f, -4f, 4f, -2f, 2f, 0f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(view, translateX)
        animator.duration = 500 // 애니메이션 지속 시간 (ms)
        animator.start()
    }

    private fun setCommitComments(comments: List<Comment>) {
        with(binding) {
            val commitCommentListRVAdapter = CommitCommentListRVAdapter(requireContext(), comments)
            commentList.adapter = commitCommentListRVAdapter
            commentList.layoutManager = LinearLayoutManager(requireContext())

            clickCommitCommentItem(commitCommentListRVAdapter, comments)
        }
    }

    // TODO: 수정 시 메세지 입력 창 같이 떠오르는 현상 없애고, edittext가 자판 위로 오도록 처리
    private fun clickCommitCommentItem(commitCommentListRVAdapter: CommitCommentListRVAdapter, commentList: List<Comment>) {
        commitCommentListRVAdapter.onClickListener = object : CommitCommentListRVAdapter.OnClickListener {
            override fun onDeleteClick(view: View, position: Int) {
                showDeleteCommentDialog(commentList[position].id)
            }

            override fun onReportClick(view: View, position: Int) {
                makeSnackBar("신고가 접수되었습니다").show()
            }
        }
    }

    private fun makeSnackBar(message: String): Snackbar {
        val snackBar = Snackbar.make(requireView(), "Red SnackBar", Snackbar.LENGTH_SHORT)
        val binding = LayoutSnackbarRedBinding.inflate(layoutInflater)

        @Suppress("RestrictedApi")
        val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout

        with(snackBarLayout) {
            removeAllViews()
            setPadding(22, 0, 22, 20)
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.TRANSPARENT))
            addView(binding.root, 0)
        }

        with(binding) {
            snackBarText.text = message
        }

        return snackBar
    }

    private fun showDeleteCommentDialog(commentId: Int) {
        val customSetDialog = CustomSetDialog(requireContext())
        customSetDialog.setAlertText(getString(R.string.study_comment_delete))
        customSetDialog.setOnConfirmClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.deleteCommitComment(commit?.id ?: 0, commentId, commit?.studyInfoId ?: 0)
            }
        }
        customSetDialog.show()
    }

    private fun setupUI(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    activity?.let { KeyboardUtils.hideKeyboard(it) }
                }
                false
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}