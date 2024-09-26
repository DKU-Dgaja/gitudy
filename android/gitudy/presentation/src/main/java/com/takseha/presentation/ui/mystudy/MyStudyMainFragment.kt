package com.takseha.presentation.ui.mystudy

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
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
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.feed.StudyPeriodStatus
import com.takseha.data.dto.feed.StudyRankResponse
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.data.dto.mystudy.Comment
import com.takseha.data.dto.mystudy.StudyInfoResponse
import com.takseha.data.dto.mystudy.StudyMember
import com.takseha.data.dto.mystudy.Todo
import com.takseha.presentation.R
import com.takseha.presentation.adapter.CategoryInStudyRVAdapter
import com.takseha.presentation.adapter.CommentListRVAdapter
import com.takseha.presentation.adapter.MemberRankRVAdapter
import com.takseha.presentation.databinding.FragmentMyStudyMainBinding
import com.takseha.presentation.ui.common.KeyboardUtils
import com.takseha.presentation.viewmodel.mystudy.MyStudyMainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class MyStudyMainFragment : Fragment() {
    private var _binding: FragmentMyStudyMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyStudyMainViewModel by activityViewModels()
    private var studyInfoId: Int = 0
    private var isLeader: Boolean? = null
    private var studyStatus: StudyStatus? = null
    private lateinit var studyImgColor: String
    private val colorList = listOf(
        R.color.BG_10,
        R.color.BG_9,
        R.color.BG_8,
        R.color.BG_7,
        R.color.BG_6,
        R.color.BG_5,
        R.color.BG_4,
        R.color.BG_3,
        R.color.BG_2,
        R.color.BG_1
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studyInfoId = requireActivity().intent.getIntExtra("studyInfoId", 0)
        isLeader = requireActivity().intent.getBooleanExtra("isLeader", false)
        studyImgColor = requireActivity().intent.getStringExtra("studyImgColor") ?: "0"
        studyStatus = requireActivity().intent.getSerializableExtra("studyStatus") as StudyStatus
        lifecycleScope.launch {
            launch { viewModel.getMyStudyInfo(studyInfoId) }
            launch { viewModel.getUrgentTodo(studyInfoId) }
            launch { viewModel.getStudyMemberList(studyInfoId) }
            launch { viewModel.getStudyRankAndScore(studyInfoId) }
            launch { viewModel.getStudyComments(studyInfoId, 3) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyStudyMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var comment = ""
        setupUI(view)

        observeViewModel()

        with(binding) {
            val bundle = Bundle().apply {
                putInt("studyInfoId", studyInfoId)
                putBoolean("isLeader", isLeader!!)
                putSerializable("studyStatus", studyStatus)
            }

            myStudyMainSwipeRefreshLayout.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    launch { viewModel.getMyStudyInfo(studyInfoId) }
                    launch { viewModel.getUrgentTodo(studyInfoId) }
                    launch { viewModel.getStudyMemberList(studyInfoId) }
                    launch { viewModel.getStudyRankAndScore(studyInfoId) }
                    launch { viewModel.getStudyComments(studyInfoId, 3) }
                    myStudyMainSwipeRefreshLayout.isRefreshing = false
                }
            }

            backBtn.setOnClickListener {
                requireActivity().finish()
            }
            settingBtn.setOnClickListener {
                view.findNavController()
                    .navigate(R.id.action_myStudyMainFragment_to_myStudySettingFragment, bundle)
            }
            copyBtn.setOnClickListener {
                val textToCopy = studyGithubLinkText.text
                val clipboard =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("githubLink", textToCopy)
                clipboard.setPrimaryClip(clip)
            }
            todoInfoBtn.setOnClickListener {
                if(todoInfoText.visibility == GONE) {
                    todoInfoText.visibility = VISIBLE
                } else {
                    todoInfoText.visibility = GONE
                }
            }
            todoBox.setOnClickListener {
                view.findNavController()
                    .navigate(R.id.action_myStudyMainFragment_to_toDoFragment, bundle)
            }
            todoMoreBtn.setOnClickListener {
                view.findNavController()
                    .navigate(R.id.action_myStudyMainFragment_to_toDoFragment, bundle)
            }
            commentMoreBtn.setOnClickListener {
                view.findNavController()
                    .navigate(R.id.action_myStudyMainFragment_to_studyCommentBoardFragment, bundle)
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
                    viewModel.makeStudyComment(studyInfoId, comment, 3)

                    newCommentBody.setText("")
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(newCommentBody.windowToken, 0)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.myStudyState.collectLatest {
                if (studyStatus != StudyStatus.STUDY_INACTIVE) {
                    requireActivity().window.statusBarColor = ContextCompat.getColor(
                        requireContext(),
                        colorList[it.myStudyInfo.profileImageUrl.toIntOrNull() ?: 0]
                    )
                } else {
                    requireActivity().window.statusBarColor = ContextCompat.getColor(
                        requireContext(),
                        R.color.GS_300
                    )
                }
                setMyStudyInfo(it.myStudyInfo.profileImageUrl, it.myStudyInfo)
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
    }

    // 원래 페이지로 돌아왔을 때 state 업데이트
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            launch { viewModel.getMyStudyInfo(studyInfoId) }
            launch { viewModel.getUrgentTodo(studyInfoId) }
            launch { viewModel.getStudyMemberList(studyInfoId) }
            launch { viewModel.getStudyRankAndScore(studyInfoId) }
            launch { viewModel.getStudyComments(studyInfoId, 3) }
        }
    }

    private fun setMyStudyInfo(
        studyImgColor: String,
        myStudyInfo: StudyInfoResponse
    ) {
        with(binding) {
            if (studyStatus != StudyStatus.STUDY_INACTIVE) {
                val studyImgSrc = setStudyImg(studyImgColor.toIntOrNull() ?: 0)
                studyImg.setImageResource(studyImgSrc)
                studyEndTag.visibility = GONE
                leaderTag.visibility = if (isLeader!!) VISIBLE else GONE
                newCommentLayout.visibility = VISIBLE
                settingBtn.visibility = VISIBLE
            } else {
                studyImg.setImageResource(R.drawable.bg_mystudy_full_default)
                leaderTag.visibility = GONE
                studyEndTag.visibility = VISIBLE
                newCommentLayout.visibility = GONE
                noTodoAlarm.setTextColor(ContextCompat.getColor(requireContext(), R.color.GS_300))
                settingBtn.visibility = GONE
            }
            studyName.text = myStudyInfo.topic
            studyRule.text = setCommitRule(myStudyInfo.periodType)
            studyInfo.text = myStudyInfo.info
            isStudyOpenText.text = setStudyStatus(myStudyInfo.status)
            studyGithubLinkText.text = getString(
                R.string.study_github_link,
                myStudyInfo.githubLinkInfo.owner,
                myStudyInfo.githubLinkInfo.name
            )
            setCategoryList(myStudyInfo.categoryNames)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUrgentTodoInfo(todoInfo: Todo?) {
        with(binding) {
            if (todoInfo != null) {
                todoLinkBtn.setOnClickListener {
                    val textToCopy = todoInfo.todoLink
                    val clipboard =
                        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("todoLink", textToCopy)
                    clipboard.setPrimaryClip(clip)
                }
                todoDetailTitle.text = todoInfo.title
                todoDetailText.text = todoInfo.detail
                todoTime.text = todoInfo.todoDate
                if (todoInfo.todoDate == LocalDate.now().toString()) {
                    todoTime.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.BASIC_RED
                        )
                    )
                }
            }
        }
    }

    private fun setStudyImg(currentIdx: Int): Int {
        return when (currentIdx) {
            0 -> R.drawable.bg_mystudy_full_10
            1 -> R.drawable.bg_mystudy_full_9
            2 -> R.drawable.bg_mystudy_full_8
            3 -> R.drawable.bg_mystudy_full_7
            4 -> R.drawable.bg_mystudy_full_6
            5 -> R.drawable.bg_mystudy_full_5
            6 -> R.drawable.bg_mystudy_full_4
            7 -> R.drawable.bg_mystudy_full_3
            8 -> R.drawable.bg_mystudy_full_2
            else -> R.drawable.bg_mystudy_full_1
        }
    }

    private fun setStudyRank(rankAndScore: StudyRankResponse) {
        with(binding) {
            studyRankText.text =
                getString(R.string.study_team_rank, rankAndScore.score, rankAndScore.ranking)
        }
    }

    private fun setCategoryList(categoryList: List<String>) {
        with(binding) {
            val categoryInStudyRVAdapter = CategoryInStudyRVAdapter(requireContext(), categoryList)
            tagList.adapter = categoryInStudyRVAdapter
            tagList.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
            else -> getString(R.string.study_inactive)
        }
    }

    private fun setStudyComments(comments: List<Comment>) {
        with(binding) {
            val commentListRVAdapter = CommentListRVAdapter(requireContext(), comments)
            commentList.adapter = commentListRVAdapter
            commentList.layoutManager = LinearLayoutManager(requireContext())
        }
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