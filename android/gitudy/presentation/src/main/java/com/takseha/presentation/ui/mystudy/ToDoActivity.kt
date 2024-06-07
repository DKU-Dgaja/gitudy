package com.takseha.presentation.ui.mystudy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.mystudy.Todo
import com.takseha.presentation.R
import com.takseha.presentation.adapter.ToDoListRVAdapter
import com.takseha.presentation.databinding.ActivityToDoBinding
import com.takseha.presentation.ui.home.MainHomeActivity
import com.takseha.presentation.viewmodel.mystudy.TodoViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ToDoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityToDoBinding
    private val viewModel: TodoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do)
        setBinding()

        val studyInfoId = intent.getIntExtra("studyInfoId", 0)

        viewModel.getTodoList(studyInfoId)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                setTodoInfo(it.todoListInfo)
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun setTodoInfo(todoList: List<Todo>) {
        with(binding) {
            val todoListRVAdapter = ToDoListRVAdapter(this@ToDoActivity, todoList)

            todoWithDateList.adapter = todoListRVAdapter
            todoWithDateList.layoutManager = LinearLayoutManager(this@ToDoActivity)
        }
    }

    private fun setBinding() {
        binding = ActivityToDoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}