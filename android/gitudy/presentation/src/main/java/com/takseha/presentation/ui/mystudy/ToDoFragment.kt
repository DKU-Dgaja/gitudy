package com.takseha.presentation.ui.mystudy

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.mystudy.Commit
import com.takseha.data.dto.mystudy.Todo
import com.takseha.presentation.R
import com.takseha.presentation.adapter.ToDoListRVAdapter
import com.takseha.presentation.databinding.FragmentToDoBinding
import com.takseha.presentation.ui.common.CustomDialog
import com.takseha.presentation.viewmodel.mystudy.TodoViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ToDoFragment : Fragment() {
    private var _binding: FragmentToDoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TodoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentToDoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val studyInfoId = activity?.intent?.getIntExtra("studyInfoId", 0) ?: 0

        viewModel.getTodoList(studyInfoId)
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                setTodoInfo(it.todoListInfo)

                // TODO: 커밋 히스토리 함께 보기 설정
                if (binding.commitWithTodoCheckBtn.isChecked) {

                } else {

                }
            }
        }

        with(binding) {
            backBtn.setOnClickListener {
                requireActivity().finish()
            }
        }
    }

    private fun setTodoInfo(todoList: List<Todo>) {
        with(binding) {
            val todoListRVAdapter = ToDoListRVAdapter(requireContext(), todoList)

            todoWithDateList.adapter = todoListRVAdapter
            todoWithDateList.layoutManager = LinearLayoutManager(requireContext())

            clickToDoItem(todoListRVAdapter, todoList)
        }
    }

    private fun clickToDoItem(todoListRVAdapter: ToDoListRVAdapter, todoList: List<Todo>) {
        todoListRVAdapter.onClickListener = object : ToDoListRVAdapter.OnClickListener {
            override fun onCommitClick(commit: Commit) {
                TODO("Not yet implemented")
            }

            override fun onUpdateClick(view: View, position: Int) {
                view.findNavController().navigate(R.id.action_toDoFragment_to_updateTodoFragment)
            }

            override fun onDeleteClick(view: View, position: Int) {
                showDeleteTodoDialog(todoList[position].studyInfoId, todoList[position].id)
            }

            override fun onLinkClick(view: View, position: Int) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun showDeleteTodoDialog(studyInfoId: Int, todoId: Int) {
        val customDialog = CustomDialog(requireContext())
        customDialog.setAlertText(getString(R.string.to_do_delete))
        customDialog.setOnConfirmClickListener {
            viewModel.deleteTodo(studyInfoId, todoId)
        }
        customDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
