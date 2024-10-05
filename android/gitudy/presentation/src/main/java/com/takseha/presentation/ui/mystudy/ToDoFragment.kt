package com.takseha.presentation.ui.mystudy

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.data.dto.mystudy.Commit
import com.takseha.data.dto.mystudy.Todo
import com.takseha.presentation.R
import com.takseha.presentation.adapter.ToDoListRVAdapter
import com.takseha.presentation.databinding.FragmentToDoBinding
import com.takseha.presentation.ui.common.CustomSetDialog
import com.takseha.presentation.viewmodel.mystudy.TodoViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ToDoFragment : Fragment() {
    private var _binding: FragmentToDoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TodoViewModel by activityViewModels()
    private var studyInfoId: Int = 0
    private var isLeader: Boolean? = null
    private var studyStatus: StudyStatus? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.WHITE)
        studyInfoId = requireActivity().intent?.getIntExtra("studyInfoId", 0) ?: 0
        isLeader = requireActivity().intent.getBooleanExtra("isLeader", false)
        studyStatus = requireActivity().intent.getSerializableExtra("studyStatus") as StudyStatus
        viewModel.getTodoList(studyInfoId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentToDoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.todoListState.collectLatest {
                if (!isLeader!!) {
                    binding.addTodoBtn.visibility = GONE
                }
                if (it.isTodoEmpty) {
                    binding.isNoTodoLayout.visibility = VISIBLE
                } else {
                    binding.isNoTodoLayout.visibility = GONE
                    setTodoList(it.todoListInfo)
                }
                // TODO: 커밋 히스토리 함께 보기 설정
//                if (binding.commitWithTodoCheckBtn.isChecked) {
//
//                } else {
//
//                }
            }
        }

        with(binding) {
            if (studyStatus == StudyStatus.STUDY_INACTIVE) addTodoBtn.visibility = GONE

            todoSwipeRefreshLayout.setOnRefreshListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.getTodoList(studyInfoId)
                    todoSwipeRefreshLayout.isRefreshing = false
                }
            }
            todoInfoBtn.setOnClickListener {
                if(todoInfoText.visibility == GONE) {
                    todoInfoText.visibility = VISIBLE
                } else {
                    todoInfoText.visibility = GONE
                }
            }
            backBtn.setOnClickListener {
                it.findNavController().popBackStack()
            }
            addTodoBtn.setOnClickListener {
                it.findNavController().navigate(R.id.action_toDoFragment_to_addTodoFragment)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.WHITE)
        viewModel.getTodoList(studyInfoId)
    }

    private fun setTodoList(todoList: List<Todo>) {
        with(binding) {
            val todoListRVAdapter = ToDoListRVAdapter(requireContext(), todoList, isLeader)

            todoWithDateList.adapter = todoListRVAdapter
            todoWithDateList.layoutManager = LinearLayoutManager(requireContext())

            clickToDoItem(todoListRVAdapter, todoList)
        }
    }

    private fun clickToDoItem(todoListRVAdapter: ToDoListRVAdapter, todoList: List<Todo>) {
        todoListRVAdapter.onClickListener = object : ToDoListRVAdapter.OnClickListener {
            override fun onCommitClick(commit: Commit) {
                val bundle = Bundle().apply {
                    putSerializable("commit", commit)
                }
                Log.d("ToDoFragment", bundle.toString())
                view!!.findNavController().navigate(R.id.action_toDoFragment_to_commitDetailFragment, bundle)
            }

            override fun onDeleteClick(view: View, position: Int) {
                showDeleteTodoDialog(todoList[position].studyInfoId, todoList[position].id)
            }

            override fun onLinkClick(view: View, position: Int) {
                val textToCopy = todoList[position].todoLink
                val clipboard =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("todoLink", textToCopy)
                clipboard.setPrimaryClip(clip)
            }
        }
    }

    private fun showDeleteTodoDialog(studyInfoId: Int, todoId: Int) {
        val customSetDialog = CustomSetDialog(requireContext())
        customSetDialog.setAlertText(getString(R.string.to_do_delete))
        customSetDialog.setOnConfirmClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.deleteTodo(studyInfoId, todoId)
            }
        }
        customSetDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
