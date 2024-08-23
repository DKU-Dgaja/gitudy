package com.takseha.presentation.ui.mystudy

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.feed.StudyPeriodStatus
import com.takseha.data.dto.feed.StudyRankResponse
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.data.dto.mystudy.StudyComment
import com.takseha.data.dto.mystudy.StudyInfoResponse
import com.takseha.data.dto.mystudy.StudyMember
import com.takseha.data.dto.mystudy.Todo
import com.takseha.presentation.R
import com.takseha.presentation.adapter.CategoryInStudyRVAdapter
import com.takseha.presentation.adapter.CommentListRVAdapter
import com.takseha.presentation.adapter.MemberRankRVAdapter
import com.takseha.presentation.databinding.FragmentMyStudyMainBinding
import com.takseha.presentation.viewmodel.mystudy.MyStudyMainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class MyStudyMainFragment : Fragment() {
    private var _binding: FragmentMyStudyMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyStudyMainViewModel by viewModels()
    private var studyInfoId: Int = 0
    private var isLeader: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyStudyMainBinding.inflate(inflater, container, false)
        return binding.root
    }
    // TODO: todo link 버튼 눌렀을 때 이동하는 기능 구현
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        studyInfoId = requireActivity().intent.getIntExtra("studyInfoId", 0)
        isLeader = requireActivity().intent.getBooleanExtra("isLeader", false)
        var comment = ""

        viewModel.getMyStudyInfo(studyInfoId)
        viewModel.getStudyComments(studyInfoId, 3)

        observeViewModel()

        with(binding) {
            val bundle = Bundle().apply {
                putInt("studyInfoId", studyInfoId)
                putBoolean("isLeader", isLeader!!)
            }
            Log.d("MyStudyMainFragment", bundle.toString())
            backBtn.setOnClickListener {
                requireActivity().finish()
            }
            settingBtn.setOnClickListener {
                view.findNavController().navigate(R.id.action_myStudyMainFragment_to_myStudySettingFragment, bundle)
            }
            todoMoreBtn.setOnClickListener {
                view.findNavController().navigate(R.id.action_myStudyMainFragment_to_toDoFragment, bundle)
            }
            commentMoreBtn.setOnClickListener {
                view.findNavController().navigate(R.id.action_myStudyMainFragment_to_studyCommentBoardFragment, bundle)
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
                lifecycleScope.launch {
                    viewModel.makeStudyComment(studyInfoId, comment, 3)

                    newCommentBody.setText("")
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(newCommentBody.windowToken, 0)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.myStudyState.collectLatest {
                requireActivity().window.statusBarColor = Color.parseColor(it.myStudyInfo.profileImageUrl)
                setMyStudyInfo(it.myStudyInfo.id, it.myStudyInfo.profileImageUrl, it.myStudyInfo)
                if (it.isUrgentTodo) {
                    with(binding) {
                        noTodoAlarm.visibility = GONE
                        setUrgentTodoInfo(it.todoInfo)
                        todoDetailLayout.visibility = VISIBLE
                        todoDetailText.visibility = VISIBLE
                    }
                } else {
                    with(binding) {
                        todoDetailLayout.visibility = GONE
                        todoDetailText.visibility = GONE
                        noTodoAlarm.visibility = VISIBLE
                        setUrgentTodoInfo(it.todoInfo)
                    }
                }
                setMemberRank(it.studyMemberListInfo)
                setStudyRank(it.rankAndScore)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.commentState.collectLatest {
                setStudyComments(it)
            }
        }
    }

    // 원래 페이지로 돌아왔을 때 state 업데이트
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.getMyStudyInfo(studyInfoId)
            viewModel.getStudyComments(studyInfoId, 3)
        }
    }

    private fun setMyStudyInfo(studyInfoId: Int, studyImgColor: String, myStudyInfo: StudyInfoResponse) {
        with(binding) {
            studyBackgroundImg.setBackgroundColor(Color.parseColor(studyImgColor))
            studyName.text = myStudyInfo.topic
            leaderTag.visibility = if (myStudyInfo.isLeader) VISIBLE else GONE
            studyRule.text = setCommitRule(myStudyInfo.periodType)
            studyInfo.text = myStudyInfo.info
            isStudyOpenText.text = setStudyStatus(myStudyInfo.status)
            studyGithubLinkText.text = getString(R.string.study_github_link, myStudyInfo.githubLinkInfo.owner, myStudyInfo.githubLinkInfo.name)
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
                            requireContext(),
                            R.color.BASIC_RED
                        ))
                }
            }
        }
    }

    private fun setStudyRank(rankAndScore: StudyRankResponse) {
        with(binding) {
            studyRankText.text = String.format(
                getString(R.string.study_team_rank),
                rankAndScore.score, rankAndScore.ranking
            )
        }
    }

    private fun setCategoryList(categoryList: List<String>) {
        with(binding) {
            val categoryInStudyRVAdapter = CategoryInStudyRVAdapter(requireContext(), categoryList)
            tagList.adapter = categoryInStudyRVAdapter
            tagList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setMemberRank(studyMemberList: List<StudyMember>) {
        with(binding) {
            val memberRankRVAdapter = MemberRankRVAdapter(requireContext(), studyMemberList)
            rankingListInTeam.adapter = memberRankRVAdapter
            rankingListInTeam.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setCommitRule(periodType: StudyPeriodStatus): String {
        return when (periodType) {
            StudyPeriodStatus.STUDY_PERIOD_EVERYDAY -> getString(R.string.feed_rule_everyday)
            StudyPeriodStatus.STUDY_PERIOD_WEEK -> getString(R.string.feed_rule_week)
            StudyPeriodStatus.STUDY_PERIOD_NONE -> getString(R.string.feed_rule_free)
        }
    }

    private fun setStudyStatus(status: StudyStatus): String {
        return when (status) {
            StudyStatus.STUDY_PRIVATE -> getString(R.string.study_lock)
            StudyStatus.STUDY_PUBLIC -> getString(R.string.study_unlock)
            StudyStatus.STUDY_DELETED -> getString(R.string.study_deleted)
        }
    }

    private fun setStudyComments(comments: List<StudyComment>) {
        with(binding) {
            val commentListRVAdapter = CommentListRVAdapter(requireContext(), comments)
            commentList.adapter = commentListRVAdapter
            commentList.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}