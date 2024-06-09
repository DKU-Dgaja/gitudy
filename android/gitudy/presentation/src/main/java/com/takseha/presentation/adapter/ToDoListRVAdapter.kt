package com.takseha.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.mystudy.Commit
import com.takseha.data.dto.mystudy.CommitStatus
import com.takseha.data.dto.mystudy.LikeCount
import com.takseha.data.dto.mystudy.Todo
import com.takseha.presentation.databinding.ItemTodoBinding
import java.time.LocalDate

class ToDoListRVAdapter(val context: Context, val todoList: List<Todo>, private val onCommitClickListener: OnCommitClickListener) :
    RecyclerView.Adapter<ToDoListRVAdapter.ViewHolder>() {
    // 임시 커밋 리스트
    private var commitList: List<Commit> = listOf(
        Commit(
            "2024-06-09",
            "5a20e4d470a60fe858e4c1b896f22c59b3981ba2",
            1,
            LikeCount(0),
            "6PHP1b #7576:Kotlin",
            "이주성",
            CommitStatus.COMMIT_APPROVAL,
            17,
            13,
            13
        ),
        Commit(
            "2024-06-09",
            "5a20e4d470a60fe858e4c1b896f22c59b3981ba2",
            1,
            LikeCount(0),
            "6PHP1b #7576:JAVA",
            "이정우",
            CommitStatus.COMMIT_REJECTION,
            17,
            13,
            14
        ),
        Commit(
            "2024-06-10",
            "5a20e4d470a60fe858e4c1b896f22c59b3981ba2",
            1,
            LikeCount(0),
            "6PHP1b #7576:C++",
            "구영민",
            CommitStatus.COMMIT_APPROVAL,
            17,
            13,
            12
        ),
        Commit(
            "2024-06-11",
            "5a20e4d470a60fe858e4c1b896f22c59b3981ba2",
            1,
            LikeCount(0),
            "6PHP1b #7576:C++",
            "탁세하",
            CommitStatus.COMMIT_WAITING,
            17,
            13,
            11
        )
    )

    interface OnCommitClickListener {
        fun onCommitClick(commit: Commit)
    }

    class ViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        var todoDate = binding.todoDate
        var todoTitle = binding.todoDetailTitle
        var todoTime = binding.todoTime
        var todoDetail = binding.todoDetailText
        var todoCode = binding.todoCode
        var todoLinkBtn = binding.todoLinkBtn
        var commitList = binding.commitList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.todoDate.text = (LocalDate.now().minusDays((position * 7).toLong())).toString()
        holder.todoTitle.text = todoList[position].title
        holder.todoTime.text = todoList[position].todoDate
        holder.todoDetail.text = todoList[position].detail
        holder.todoCode.text = todoList[position].todoCode
        holder.todoLinkBtn.setOnClickListener {
            // todoList[position].todoLink웹뷰로 이동
        }
        // setCommitList(holder.commitList, todoList[position].commitList)
        setCommitList(holder.commitList, commitList)
    }

    private fun setCommitList(commitListView: RecyclerView, commitList: List<Commit>) {
        val commitListRVAdapter = CommitListRVAdapter(context, commitList, onCommitClickListener)

        commitListView.adapter = commitListRVAdapter
        commitListView.layoutManager = LinearLayoutManager(context)
    }

    override fun getItemCount(): Int {
        return todoList.size
    }
}