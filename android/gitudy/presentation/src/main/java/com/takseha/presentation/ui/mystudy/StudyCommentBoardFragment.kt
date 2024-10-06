package com.takseha.presentation.ui.mystudy

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.google.android.material.snackbar.Snackbar
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.data.dto.mystudy.Comment
import com.takseha.presentation.R
import com.takseha.presentation.adapter.DetailCommentListRVAdapter
import com.takseha.presentation.databinding.FragmentStudyCommentBoardBinding
import com.takseha.presentation.databinding.LayoutSnackbarRedBinding
import com.takseha.presentation.ui.common.CustomSetDialog
import com.takseha.presentation.ui.common.KeyboardUtils
import com.takseha.presentation.viewmodel.mystudy.StudyCommentBoardViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StudyCommentBoardFragment : Fragment() {
    private var _binding: FragmentStudyCommentBoardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StudyCommentBoardViewModel by activityViewModels()
    private var studyInfoId: Int = 0
    private var studyStatus: StudyStatus? = null
    private var comment = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.WHITE)
        studyInfoId = requireActivity().intent.getIntExtra("studyInfoId", 0)
        studyStatus = requireActivity().intent.getSerializableExtra("studyStatus") as StudyStatus
        viewModel.getStudyComments(studyInfoId, 10)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudyCommentBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)

        // TODO: limit 무한스크롤 관련 구현
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.commentState.collectLatest {
                if (it != null) {
                    if (it.isEmpty()) {
                        binding.isNoCommentLayout.visibility = VISIBLE
                    } else {
                        binding.isNoCommentLayout.visibility = GONE
                    }
                    setStudyComments(it)
                }
            }
        }
        with(binding) {
            if (studyStatus == StudyStatus.STUDY_INACTIVE) messageLayout.visibility = GONE

            commentSwipeRefreshLayout.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.getStudyComments(studyInfoId, 50)
                    commentSwipeRefreshLayout.isRefreshing = false
                }
            }
                backBtn.setOnClickListener {
                it.findNavController().popBackStack()
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
                    viewModel.makeStudyComment(studyInfoId, comment, 10)
                    newCommentBody.setText("")
                }
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(newCommentBody.windowToken, 0)
            }
        }
    }

    private fun setStudyComments(comments: List<Comment>) {
        with(binding) {
            val commentDetailListRVAdapter = DetailCommentListRVAdapter(requireContext(), comments)
            commentList.adapter = commentDetailListRVAdapter
            commentList.layoutManager = LinearLayoutManager(requireContext())

            clickStudyCommentItem(commentDetailListRVAdapter, comments)
        }
    }

    // TODO: 수정 시 메세지 입력 창 같이 떠오르는 현상 없애고, edittext가 자판 위로 오도록 처리
    private fun clickStudyCommentItem(commentDetailListRVAdapter: DetailCommentListRVAdapter, commentList: List<Comment>) {
        commentDetailListRVAdapter.onClickListener = object : DetailCommentListRVAdapter.OnClickListener {
            override fun onDeleteClick(view: View, position: Int) {
                showDeleteCommentDialog(commentList[position].studyInfoId, commentList[position].id)
            }

            override fun onLikeClick(view: View, position: Int) {
                shakeBtn(view)
            }

            override fun onHeartClick(view: View, position: Int) {
                shakeBtn(view)
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

    private fun showDeleteCommentDialog(studyInfoId: Int, studyCommentId: Int) {
        val customSetDialog = CustomSetDialog(requireContext())
        customSetDialog.setAlertText(getString(R.string.study_comment_delete))
        customSetDialog.setOnConfirmClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.deleteStudyComment(studyInfoId, studyCommentId, 10)
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