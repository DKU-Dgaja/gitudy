package com.takseha.presentation.ui.mystudy

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.mystudy.StudyComment
import com.takseha.presentation.R
import com.takseha.presentation.adapter.CommentListRVAdapter
import com.takseha.presentation.databinding.ActivityStudyCommentBoardBinding
import com.takseha.presentation.ui.common.CustomDialog
import com.takseha.presentation.viewmodel.mystudy.MyStudyMainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// TODO: Android Studio 업데이트 후 생성한 Activity: 기본 Activity 양식 변경됨 -> 추후 통일하기
class StudyCommentBoardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudyCommentBoardBinding
    private val viewModel: MyStudyMainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_comment_board)
        setBinding()
        window.statusBarColor = ContextCompat.getColor(this, R.color.BACKGROUND)

        val studyInfoId = intent.getIntExtra("studyInfoId", 0)
        var comment = ""

        // TODO: limit 무한스크롤 관련 구현
        viewModel.getStudyComments(studyInfoId, 10)
        observeViewModel()

        with(binding) {
            backBtn.setOnClickListener {
                finish()
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
                viewModel.makeStudyComment(studyInfoId, comment, 10)

                newCommentBody.setText("")
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(newCommentBody.windowToken, 0)
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.commentState.collectLatest {
                setStudyComments(it)
            }
        }
    }

    private fun setStudyComments(comments: List<StudyComment>) {
        with(binding) {
            val commentListRVAdapter = CommentListRVAdapter(this@StudyCommentBoardActivity, comments)
            commentList.adapter = commentListRVAdapter
            commentList.layoutManager = LinearLayoutManager(this@StudyCommentBoardActivity)

            clickStudyCommentItem(commentListRVAdapter, comments)
        }
    }

    // TODO: 수정 시 메세지 입력 창 같이 떠오르는 현상 없애고, edittext가 자판 위로 오도록 처리
    private fun clickStudyCommentItem(commentListRVAdapter: CommentListRVAdapter, commentList: List<StudyComment>) {
        commentListRVAdapter.onClickListener = object : CommentListRVAdapter.OnClickListener {
            override fun onUpdateClick(view: View, position: Int) {
                val commentViewHolder = binding.commentList.findViewHolderForAdapterPosition(position) as CommentListRVAdapter.ViewHolder
                var contentBody = commentViewHolder.binding.contentEditText
                var confirmBtn = commentViewHolder.binding.confirmBtn
                var content: String

                contentBody.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

                    override fun afterTextChanged(s: Editable?) {
                        content = contentBody.text.toString()
                        confirmBtn.isEnabled = content.isNotEmpty()

                        if (confirmBtn.isEnabled) confirmBtn.setTextColor(ContextCompat.getColor(this@StudyCommentBoardActivity, R.color.GS_700))
                        else confirmBtn.setTextColor(ContextCompat.getColor(this@StudyCommentBoardActivity, R.color.GS_400))
                    }
                })
            }

            override fun onConfirmClick(view: View, position: Int) {
                val commentViewHolder = binding.commentList.findViewHolderForAdapterPosition(position) as CommentListRVAdapter.ViewHolder
                var content = commentViewHolder.binding.contentEditText.text.toString()

                showUpdateCommentDialog(commentList[position].studyInfoId, commentList[position].id, content)
            }

            override fun onDeleteClick(view: View, position: Int) {
                showDeleteCommentDialog(commentList[position].studyInfoId, commentList[position].id)
            }
        }
    }

    private fun showUpdateCommentDialog(studyInfoId: Int, studyCommentId: Int, content: String) {
        val customDialog = CustomDialog(this)
        customDialog.setAlertText(getString(R.string.study_comment_update))
        customDialog.setOnConfirmClickListener {
            viewModel.updateStudyComment(studyInfoId, studyCommentId, content, 10)
        }
        customDialog.setOnCancelClickListener {
            viewModel.getStudyComments(studyInfoId, 10)
        }
        customDialog.show()
    }

    private fun showDeleteCommentDialog(studyInfoId: Int, studyCommentId: Int) {
        val customDialog = CustomDialog(this)
        customDialog.setAlertText(getString(R.string.study_comment_delete))
        customDialog.setOnConfirmClickListener {
            viewModel.deleteStudyComment(studyInfoId, studyCommentId, 10)
        }
        customDialog.show()
    }

    private fun setBinding() {
        binding = ActivityStudyCommentBoardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}