package com.takseha.presentation.ui.mystudy

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.mystudy.StudyComment
import com.takseha.presentation.R
import com.takseha.presentation.adapter.CommentListRVAdapter
import com.takseha.presentation.databinding.ActivityMyStudyMainBinding
import com.takseha.presentation.databinding.ActivityStudyCommentBoardBinding
import com.takseha.presentation.ui.home.MainHomeActivity
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
        }
    }

    private fun setBinding() {
        binding = ActivityStudyCommentBoardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}