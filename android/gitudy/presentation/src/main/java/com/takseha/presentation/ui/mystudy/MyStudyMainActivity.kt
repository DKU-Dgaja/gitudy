package com.takseha.presentation.ui.mystudy

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.feed.StudyPeriod
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.data.dto.mystudy.MyStudyInfo
import com.takseha.data.dto.mystudy.StudyConvention
import com.takseha.data.dto.mystudy.StudyMember
import com.takseha.data.dto.mystudy.Todo
import com.takseha.presentation.R
import com.takseha.presentation.adapter.MemberRankRVAdapter
import com.takseha.presentation.adapter.MyStudyRVAdapter
import com.takseha.presentation.databinding.ActivityMyStudyMainBinding
import com.takseha.presentation.ui.home.MainHomeActivity
import com.takseha.presentation.viewmodel.mystudy.MyStudyMainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.abs

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
         */

        val studyInfoId = intent.getIntExtra("studyInfoId", 0)
        val studyImgColor = intent.getStringExtra("studyImgColor")
        Log.d("MyStudyMainActivity", studyInfoId.toString())

        window.statusBarColor = Color.parseColor(studyImgColor)

        viewModel.getMyStudyInfo(studyInfoId)
        setTotalInfo(studyInfoId, studyImgColor!!)

        with(binding) {
            backBtn.setOnClickListener {
                startActivity(Intent(this@MyStudyMainActivity, MainHomeActivity::class.java))
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
                intent.putExtra("conventionInfo", viewModel.uiState.value.conventionInfo)
                startActivity(intent)
            }
            swipeRefreshMyStudyMain.setOnRefreshListener {
                viewModel.getMyStudyInfo(studyInfoId)
                swipeRefreshMyStudyMain.isRefreshing = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setTotalInfo(studyInfoId: Int, studyImgColor: String) {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                setMyStudyInfo(studyInfoId, studyImgColor, it.myStudyInfo)
                setTodoInfo(it.todoInfo)
                setConventionInfo(it.conventionInfo)
                setMemberRank(it.studyMemberListInfo)
            }
        }
    }

    private fun setMyStudyInfo(studyInfoId: Int, studyImgColor: String, myStudyInfo: MyStudyInfo) {
        with(binding) {
            studyBackgroundImg.setBackgroundColor(Color.parseColor(studyImgColor))
            studyName.text = myStudyInfo.topic
            studyRule.text = setCommitRule(myStudyInfo.periodType)
            studyInfo.text = myStudyInfo.info
            isStudyOpenText.text = setStudyStatus(myStudyInfo.status)
            studyRankText.text = String.format(
                getString(R.string.study_team_rank),
                300 - studyInfoId * 10, studyInfoId - 15
            )
            studyGithubLinkText.text = myStudyInfo.githubLinkInfo.branchName
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setTodoInfo(todoInfo: Todo?) {
        with(binding) {
            if (todoInfo == null) {
                todoDetailLayout.visibility = View.GONE
                todoDetailBody.visibility = View.GONE
                noTodoAlarm.visibility = View.VISIBLE
            } else {
                todoDetailLayout.visibility = View.VISIBLE
                todoDetailBody.visibility = View.VISIBLE
                noTodoAlarm.visibility = View.GONE

                todoDetailTitle.text = todoInfo.title
                todoDetailText.text = todoInfo.detail
                todoTime.text = todoInfo.todoDate
                todoCode.text = todoInfo.todoCode
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
            if (conventionInfo == null) {
                commitConventionText.visibility = View.GONE
                noConventionAlarm.visibility = View.VISIBLE
            } else {
                commitConventionText.visibility = View.VISIBLE
                noConventionAlarm.visibility = View.GONE

                commitConvention.text = conventionInfo.name
            }
        }
    }

    private fun setMemberRank(studyMemberList: List<StudyMember>) {
        with(binding) {
            val memberRankRVAdapter = MemberRankRVAdapter(this@MyStudyMainActivity, studyMemberList)

            rankingListInTeam.adapter = memberRankRVAdapter
            rankingListInTeam.layoutManager = LinearLayoutManager(this@MyStudyMainActivity)
        }
    }

    private fun setCommitRule(periodType: StudyPeriod): String {
        when (periodType) {
            StudyPeriod.STUDY_PERIOD_EVERYDAY -> return baseContext.getString(R.string.feed_rule_everyday)
            StudyPeriod.STUDY_PERIOD_WEEK -> return baseContext.getString(R.string.feed_rule_week)
            StudyPeriod.STUDY_PERIOD_NONE -> return baseContext.getString(R.string.feed_rule_free)
        }
    }

    private fun setStudyStatus(status: StudyStatus): String {
        when (status) {
            StudyStatus.STUDY_PRIVATE -> return baseContext.getString(R.string.study_lock)
            StudyStatus.STUDY_PUBLIC -> return baseContext.getString(R.string.study_unlock)
            StudyStatus.STUDY_DELETED -> return baseContext.getString(R.string.study_deleted)
        }
    }

    private fun setBinding() {
        binding = ActivityMyStudyMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}