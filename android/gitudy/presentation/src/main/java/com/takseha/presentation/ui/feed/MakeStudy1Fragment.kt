package com.takseha.presentation.ui.feed

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
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.takseha.data.dto.feed.Category
import com.takseha.presentation.R
import com.takseha.presentation.adapter.AllCategoryRVAdapter
import com.takseha.presentation.databinding.FragmentMakeStudy1Binding
import com.takseha.presentation.ui.common.KeyboardUtils
import com.takseha.presentation.viewmodel.feed.MakeStudyViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MakeStudy1Fragment : Fragment() {
    private var _binding: FragmentMakeStudy1Binding? = null
    private val binding get() = _binding!!
    private val viewModel: MakeStudyViewModel by activityViewModels()
    private var categories = ArrayList<String>()
    private val maxLength = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAllCategory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMakeStudy1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categoryState.collectLatest {
                setCategoryList(it)
            }
        }

        with(binding) {
            var title = studyNameEditText.text.toString()
            var detail = studyDetailEditText.text.toString()
            var githubRepo = studyGithubLinkEditText.text.toString()

            studyNameEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    titleTextLength.text =
                        getString(R.string.text_length, studyNameEditText.text.length, maxLength)
                    title = studyNameEditText.text.toString()
                    nextBtn.isEnabled =
                        title.isNotEmpty() && detail.isNotEmpty() && validationCheckedImg.visibility == VISIBLE
                }
            })
            studyDetailEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    detail = studyDetailEditText.text.toString()
                    nextBtn.isEnabled =
                        title.isNotEmpty() && detail.isNotEmpty() && validationCheckedImg.visibility == VISIBLE
                }
            })
            studyGithubLinkEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val repoNameLength = studyGithubLinkEditText.length()
                    repoDesc.apply {
                        text = getString(R.string.feed_make_study_github_repo_desc)
                        setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.GS_400
                            )
                        )
                    }
                    validationCheckedImg.visibility = GONE
                    isValidNameBtn.visibility = VISIBLE
                    nextBtn.isEnabled = false    // 확인 버튼 초기화
                    isValidNameBtn.isEnabled = repoNameLength > 0
                }

                override fun afterTextChanged(s: Editable?) {
                    s?.let {
                        val newText = it.toString().replace(" ", "-")
                        if (newText != it.toString()) {
                            studyGithubLinkEditText.setText(newText)
                            studyGithubLinkEditText.setSelection(newText.length)
                        }
                    }

                    githubRepo = studyGithubLinkEditText.text.toString()
                }
            })

            isValidNameBtn.setOnClickListener {
                repoDesc.text = ""
                if (isValidNickname(githubRepo) != 4) {
                    nextBtn.isEnabled = false
                    when (isValidNickname(githubRepo)) {
                        1 -> repoDesc.apply {
                            text = "연속된 특수문자(., -, _)가 존재해요"
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.BASIC_RED
                                )
                            )
                        }

                        2 -> repoDesc.apply {
                            text = "영문, 숫자, ., -, _ 만 입력해주세요"
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.BASIC_RED
                                )
                            )
                        }

                        3 -> repoDesc.apply {
                            text = "., -, _ 로 끝나지 않는 이름을 입력해주세요"
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.BASIC_RED
                                )
                            )
                        }
                    }
                } else {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.resetCorrectRepoName()
                        viewModel.checkValidRepoName(githubRepo)

                        viewModel.isValidRepoName.collectLatest {
                            Log.e("MakeStudy1Fragment", it.toString())
                            when (it) {
                                null -> {
                                    repoDesc.visibility = GONE
                                    waitImg.visibility = VISIBLE
                                }

                                true -> {
                                    repoDesc.visibility = VISIBLE
                                    waitImg.visibility = GONE
                                    repoDesc.apply {
                                        text = "생성 가능한 레포지토리 이름이에요"
                                        setTextColor(
                                            ContextCompat.getColor(
                                                requireContext(),
                                                R.color.BASIC_GREEN
                                            )
                                        )
                                    }
                                    validationCheckedImg.visibility = VISIBLE
                                    isValidNameBtn.visibility = GONE
                                    nextBtn.isEnabled =
                                        title.isNotEmpty() && detail.isNotEmpty() && validationCheckedImg.visibility == VISIBLE
                                }

                                false -> {
                                    repoDesc.visibility = VISIBLE
                                    waitImg.visibility = GONE
                                    repoDesc.apply {
                                        text = "동일한 레포지토리 이름이 존재해요"
                                        setTextColor(
                                            ContextCompat.getColor(
                                                requireContext(),
                                                R.color.BASIC_RED
                                            )
                                        )
                                    }
                                    validationCheckedImg.visibility = GONE
                                    nextBtn.isEnabled = false
                                }
                            }
                        }
                    }
                }
            }

            nextBtn.setOnClickListener {
                val categoryIdList =
                    (categoryListRecyclerView.adapter as? AllCategoryRVAdapter)?.getSelectedItems()
                        ?: emptyList()
                viewModel.setStudyIntro(title, detail, githubRepo, categoryIdList)
                val bundle = Bundle().apply {
                    putStringArrayList("categories", categories)
                }
                it.findNavController()
                    .navigate(R.id.action_makeStudy1Fragment_to_makeStudy2Fragment, bundle)
            }
            exitBtn.setOnClickListener {
                requireActivity().finish()
            }
        }
    }

    private fun setCategoryList(categoryList: List<Category>) {
        with(binding) {
            val allCategoryRVAdapter = AllCategoryRVAdapter(requireContext(), categoryList)

            categoryListRecyclerView.adapter = allCategoryRVAdapter
            categoryListRecyclerView.layoutManager =
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)

            clickCategoryItem(allCategoryRVAdapter, categoryList)
        }
    }

    private fun clickCategoryItem(
        allCategoryRVAdapter: AllCategoryRVAdapter,
        categoryList: List<Category>
    ) {
        allCategoryRVAdapter.itemClick = object : AllCategoryRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                if (categories.contains(categoryList[position].name)) {
                    categories.remove(categoryList[position].name)
                } else {
                    categories.add(categoryList[position].name)
                }
            }
        }
    }

    private fun isValidNickname(text: String): Int {
        // 조건 1: 연속된 특수 문자가 존재할 때
        val specialCharSequenceRegex = "[_.-]{2,}"
        if (specialCharSequenceRegex.toRegex().containsMatchIn(text)) {
            return 1
        }

        // 조건 2: 유효한 문자(영문자, 숫자, ., _, -) 외에 다른 문자가 포함될 때
        val validCharRegex = "^[a-zA-Z0-9._-]*$"
        if (!validCharRegex.toRegex().matches(text)) {
            return 2
        }

        // 조건 3: 닉네임이 _, ., -로 끝날 때
        val endsWithInvalidCharRegex = "[_.-]$"
        if (endsWithInvalidCharRegex.toRegex().containsMatchIn(text)) {
            return 3
        }

        // 모든 조건을 만족
        return 4
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