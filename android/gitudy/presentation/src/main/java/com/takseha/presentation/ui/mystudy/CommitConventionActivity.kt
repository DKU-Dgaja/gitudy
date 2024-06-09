package com.takseha.presentation.ui.mystudy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import com.takseha.data.dto.mystudy.StudyConvention
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
        val conventionInfo =
            if (intent.getSerializableExtra("conventionInfo") == null) StudyConvention(
                false,
                "",
                0,
                "",
                "",
                studyInfoId
            ) else intent.getSerializableExtra("conventionInfo") as StudyConvention

        with(binding) {
            var convention = conventionInfo.name
            var content = conventionInfo.content
            var description = conventionInfo.description
            var active = conventionInfo.active

            setInitConvention(convention, content, description, active)

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
                    saveBtn.text = "저장하기"
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
                    saveBtn.text = "저장하기"
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
                    saveBtn.isEnabled =
                        convention.isNotEmpty() && content.isNotEmpty()
                    saveBtn.text = "저장하기"
                }
            })

            backBtn.setOnClickListener {
                finish()
            }

            saveBtn.setOnClickListener {
                active = true
                viewModel.setConvention(studyInfoId, convention, description, content, active)

                isConventionActive(active)

                saveBtn.isEnabled = false
                saveBtn.text = "저장 완료"
            }
        }
    }

    private fun setInitConvention(
        convention: String,
        content: String,
        description: String,
        active: Boolean
    ) {
        with(binding) {
            conventionEditText.setText(convention)
            contentEditText.setText(content)
            descriptionEditText.setText(description)
            isConventionActive(active)
        }
    }

    private fun isConventionActive(active: Boolean) {
        with(binding) {
            if (active) {
                isNotConventionText.visibility = View.GONE
                isConventionText.visibility = View.VISIBLE
            } else {
                isNotConventionText.visibility = View.VISIBLE
                isConventionText.visibility = View.GONE
            }
        }
    }

    private fun setBinding() {
        binding = ActivityCommitConventionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}