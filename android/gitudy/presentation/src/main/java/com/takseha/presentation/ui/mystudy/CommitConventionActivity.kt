package com.takseha.presentation.ui.mystudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ActivityCommitConventionBinding
import com.takseha.presentation.viewmodel.mystudy.CommitConventionViewModel

class CommitConventionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommitConventionBinding
    private val viewModel: CommitConventionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commit_convention)
        setBinding()

        val studyInfoId = intent.getIntExtra("studyInfoId", 0)

        with(binding) {
            var convention = ""
            var content = ""
            var description = ""
            var active = false // 추후 커밋 컨벤션 불러오기 api 호출 후 맞게 적용

            conventionEditText.addTextChangedListener(object : TextWatcher {
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
                    convention = conventionEditText.text.toString()
                    saveBtn.isEnabled =
                        convention.isNotEmpty() && content.isNotEmpty()
                }
            })

            contentEditText.addTextChangedListener(object : TextWatcher {
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
                    content = contentEditText.text.toString()
                    saveBtn.isEnabled =
                        convention.isNotEmpty() && content.isNotEmpty()
                }
            })

            descriptionEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    description = descriptionEditText.text.toString()
                }
            })

            backBtn.setOnClickListener {
                finish()
            }

            saveBtn.setOnClickListener {
                active = true
                viewModel.setConvention(studyInfoId, convention, description, content, active)

                isNotConventionText.visibility = View.GONE
                isConventionText.visibility = View.VISIBLE

                saveBtn.isEnabled = false
                saveBtn.text = "저장 완료"
            }
        }
    }

    private fun setBinding() {
        binding = ActivityCommitConventionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}