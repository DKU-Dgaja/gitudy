package com.takseha.presentation.ui.mystudy

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.feed.Category
import com.takseha.data.dto.feed.StudyPeriodStatus
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.data.dto.mystudy.StudyComment
import com.takseha.data.dto.mystudy.StudyConvention
import com.takseha.data.dto.mystudy.StudyInfoResponse
import com.takseha.data.dto.mystudy.StudyMember
import com.takseha.data.dto.mystudy.Todo
import com.takseha.presentation.R
import com.takseha.presentation.adapter.CategoryInStudyRVAdapter
import com.takseha.presentation.adapter.CommentListRVAdapter
import com.takseha.presentation.adapter.MemberRankRVAdapter
import com.takseha.presentation.databinding.ActivityMyStudyMainBinding
import com.takseha.presentation.ui.common.CustomDialog
import com.takseha.presentation.ui.home.MainHomeActivity
import com.takseha.presentation.viewmodel.mystudy.MyStudyMainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class MyStudyMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyStudyMainBinding
    private val viewModel: MyStudyMainViewModel by viewModels()
    private var firstTodoLink = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_study_main)
        setBinding()

        /*
        Todo: 팀장/팀원 판별해서 todo 등록 버튼 유무 정하기
        Todo: todo link 버튼 눌렀을 떄 webView 나타나게 하기
        Todo: StudyColor
         */

        val studyInfoId = intent.getIntExtra("studyInfoId", 0)
        var comment = ""

        viewModel.getMyStudyInfo(studyInfoId)
        viewModel.getStudyComments(studyInfoId, 3)

        observeViewModel()

        with(binding) {
            backBtn.setOnClickListener {
                finish()
            }
            todoMoreBtn.setOnClickListener {
                val intent = Intent(this@MyStudyMainActivity, ToDoActivity::class.java)
                intent.putExtra("studyInfoId", studyInfoId)
                startActivity(intent)
            }
            todoAdditionBtn.setOnClickListener {
                val intent = Intent(this@MyStudyMainActivity, AddTodoActivity::class.java)
                intent.putExtra("studyInfoId", studyInfoId)
                startActivity(intent)
            }
            conventionLayout.setOnClickListener {
                val intent = Intent(this@MyStudyMainActivity, CommitConventionActivity::class.java)
                intent.putExtra("studyInfoId", studyInfoId)
                intent.putExtra("conventionInfo", viewModel.myStudyState.value.conventionInfo)
                startActivity(intent)
            }
            commentMoreBtn.setOnClickListener {
                val intent = Intent(this@MyStudyMainActivity, StudyCommentBoardActivity::class.java)
                intent.putExtra("studyInfoId", studyInfoId)
                startActivity(intent)
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
                viewModel.makeStudyComment(studyInfoId, comment, 3)

                newCommentBody.setText("")
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(newCommentBody.windowToken, 0)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.myStudyState.collectLatest {
                window.statusBarColor = Color.parseColor(it.myStudyInfo.profileImageUrl)
                setMyStudyInfo(it.myStudyInfo.id, it.myStudyInfo.profileImageUrl, it.myStudyInfo)
                if (it.isUrgentTodo) {
                    with(binding) {
                        noTodoAlarm.visibility = GONE
                        setUrgentTodoInfo(it.todoInfo)
                        todoDetailLayout.visibility = VISIBLE
                        todoDetailBody.visibility = VISIBLE
                    }
                } else {
                    with(binding) {
                        todoDetailLayout.visibility = GONE
                        todoDetailBody.visibility = GONE
                        noTodoAlarm.visibility = VISIBLE
                    }
                }
                setConventionInfo(it.conventionInfo)
                setMemberRank(it.studyMemberListInfo)
            }
        }

        lifecycleScope.launch {
            viewModel.commentState.collectLatest {
                setStudyComments(it)
            }
        }
    }

    // 원래 페이지로 돌아왔을 때 state 업데이트
    override fun onResume() {
        super.onResume()
        val studyInfoId = intent.getIntExtra("studyInfoId", 0)
        viewModel.getMyStudyInfo(studyInfoId)
        viewModel.getStudyComments(studyInfoId, 3)
    }

    private fun setMyStudyInfo(studyInfoId: Int, studyImgColor: String, myStudyInfo: StudyInfoResponse) {
        with(binding) {
            studyBackgroundImg.setBackgroundColor(Color.parseColor(studyImgColor))
            studyName.text = myStudyInfo.topic
            studyRule.text = setCommitRule(myStudyInfo.periodType)
            studyInfo.text = myStudyInfo.info
            isStudyOpenText.text = setStudyStatus(myStudyInfo.status)
            studyRankText.text = String.format(
                getString(R.string.study_team_rank),
                myStudyInfo.score, studyInfoId - 15
            )
            studyGithubLinkText.text = myStudyInfo.githubLinkInfo.branchName
            setCategoryList(myStudyInfo.categoryNames)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUrgentTodoInfo(todoInfo: Todo?) {
        with(binding) {
            if (todoInfo != null) {
                todoDetailTitle.text = todoInfo.title
                todoDetailText.text = todoInfo.detail
                todoTime.text = todoInfo.todoDate
                if (todoInfo.todoDate == LocalDate.now().toString()) {
                    todoTime.setTextColor(
                        ContextCompat.getColor(
                            this@MyStudyMainActivity,
                            R.color.BASIC_RED
                        ))
                }
                firstTodoLink = todoInfo.todoLink
            }
        }
    }

    private fun setConventionInfo(conventionInfo: StudyConvention?) {
        with(binding) {
            commitConventionText.visibility = View.VISIBLE
            commitConvention.text = conventionInfo?.name
        }
    }

    private fun setCategoryList(categoryList: List<String>) {
        with(binding) {
            val categoryInStudyRVAdapter = CategoryInStudyRVAdapter(this@MyStudyMainActivity, categoryList)
            tagList.adapter = categoryInStudyRVAdapter
            tagList.layoutManager = LinearLayoutManager(this@MyStudyMainActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setMemberRank(studyMemberList: List<StudyMember>) {
        with(binding) {
            val memberRankRVAdapter = MemberRankRVAdapter(this@MyStudyMainActivity, studyMemberList)
            rankingListInTeam.adapter = memberRankRVAdapter
            rankingListInTeam.layoutManager = LinearLayoutManager(this@MyStudyMainActivity)
        }
    }

    private fun setCommitRule(periodType: StudyPeriodStatus): String {
        when (periodType) {
            StudyPeriodStatus.STUDY_PERIOD_EVERYDAY -> return baseContext.getString(R.string.feed_rule_everyday)
            StudyPeriodStatus.STUDY_PERIOD_WEEK -> return baseContext.getString(R.string.feed_rule_week)
            StudyPeriodStatus.STUDY_PERIOD_NONE -> return baseContext.getString(R.string.feed_rule_free)
        }
    }

    private fun setStudyStatus(status: StudyStatus): String {
        when (status) {
            StudyStatus.STUDY_PRIVATE -> return baseContext.getString(R.string.study_lock)
            StudyStatus.STUDY_PUBLIC -> return baseContext.getString(R.string.study_unlock)
            StudyStatus.STUDY_DELETED -> return baseContext.getString(R.string.study_deleted)
        }
    }

    private fun setStudyComments(comments: List<StudyComment>) {
        with(binding) {
            val commentListRVAdapter = CommentListRVAdapter(this@MyStudyMainActivity, comments)
            commentList.adapter = commentListRVAdapter
            commentList.layoutManager = LinearLayoutManager(this@MyStudyMainActivity)
        }
    }

    private fun setBinding() {
        binding = ActivityMyStudyMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}