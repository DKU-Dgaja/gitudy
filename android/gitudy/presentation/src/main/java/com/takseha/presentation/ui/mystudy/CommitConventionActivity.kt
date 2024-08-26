package com.takseha.presentation.ui.mystudy

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

            backBtn.setOnClickListener {
                finish()
            }
        }
    }

    private fun setBinding() {
        binding = ActivityCommitConventionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}