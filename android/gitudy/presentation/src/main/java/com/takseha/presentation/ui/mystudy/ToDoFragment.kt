package com.takseha.presentation.ui.mystudy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
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
            viewModel.todoListState.collectLatest {
                if (it.isTodoEmpty) {
                    binding.isNoTodoLayout.visibility = VISIBLE
                } else {
                    binding.isNoTodoLayout.visibility = GONE
                    setTodoList(it.todoListInfo)
                }
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

    override fun onResume() {
        super.onResume()
        val studyInfoId = activity?.intent?.getIntExtra("studyInfoId", 0) ?: 0
        viewModel.getTodoList(studyInfoId)
    }

    private fun setTodoList(todoList: List<Todo>) {
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
                val bundle = Bundle().apply {
                    putInt("commitId", commit.id)
                }
                view?.findNavController()?.navigate(R.id.action_toDoFragment_to_myCommitFragment, bundle)
            }

            override fun onUpdateClick(view: View, position: Int) {
                val bundle = Bundle().apply {
                    putInt("todoId", todoList[position].id)
                }
                view.findNavController().navigate(R.id.action_toDoFragment_to_updateTodoFragment, bundle)
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
