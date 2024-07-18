package com.takseha.presentation.ui.feed

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.takseha.presentation.R
import com.takseha.presentation.databinding.FragmentMakeStudy1Binding
import com.takseha.presentation.viewmodel.feed.MakeStudyViewModel

class MakeStudy1Fragment : Fragment() {
    private var _binding: FragmentMakeStudy1Binding? = null
    private val binding get() = _binding!!
    private val viewModel: MakeStudyViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    title = studyNameEditText.text.toString()
                    nextBtn.isEnabled =
                        title.isNotEmpty() && detail.isNotEmpty() && githubRepo.isNotEmpty()
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
                        title.isNotEmpty() && detail.isNotEmpty() && githubRepo.isNotEmpty()
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

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    githubRepo = studyGithubLinkEditText.text.toString()
                    nextBtn.isEnabled =
                        title.isNotEmpty() && detail.isNotEmpty() && githubRepo.isNotEmpty()
                }
            })
            // TODO : edittext 키보드 위로 올라가도록 하는 기능 구현
            nextBtn.setOnClickListener {
                viewModel.setStudyIntro(title, detail, githubRepo)
                Log.d("MakeStudy1Fragment", viewModel.newStudyInfoState.value.toString())
                it.findNavController()
                    .navigate(R.id.action_makeStudy1Fragment_to_makeStudy2Fragment)
            }
            exitBtn.setOnClickListener {
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}