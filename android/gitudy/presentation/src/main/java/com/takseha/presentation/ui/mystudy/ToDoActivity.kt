package com.takseha.presentation.ui.mystudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.mystudy.Commit
import com.takseha.presentation.R
import com.takseha.presentation.adapter.ToDoListRVAdapter
import com.takseha.presentation.databinding.ActivityToDoBinding
import com.takseha.presentation.ui.profile.MyCommitFragment
import com.takseha.presentation.viewmodel.mystudy.TodoViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ToDoActivity : AppCompatActivity(), ToDoListRVAdapter.OnCommitClickListener  {
    private lateinit var binding: ActivityToDoBinding
    private val viewModel: TodoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do)
        setBinding()
        window.statusBarColor = getColor(R.color.WHITE)

        val studyInfoId = intent.getIntExtra("studyInfoId", 0)

        viewModel.getTodoList(studyInfoId)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                val todoListRVAdapter = ToDoListRVAdapter(this@ToDoActivity, it.todoListInfo, this@ToDoActivity)
                setTodoInfo(todoListRVAdapter)

                // TODO: 커밋 히스토리 함께 보기 설정
                if (binding.commitWithTodoCheckBtn.isChecked) {

                } else {

                }
            }
        }

        with(binding) {
            backBtn.setOnClickListener {
                finish()
            }
        }
    }

    private fun setTodoInfo(todoListRVAdapter: ToDoListRVAdapter) {
        with(binding) {
            todoWithDateList.adapter = todoListRVAdapter
            todoWithDateList.layoutManager = LinearLayoutManager(this@ToDoActivity)
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.vertical_enter, R.anim.vertical_exit)
            .replace(R.id.CommitFragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onCommitClick(commit: Commit) {
        navigateToFragment(MyCommitFragment.newInstance(commit))
    }

    private fun setBinding() {
        binding = ActivityToDoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}