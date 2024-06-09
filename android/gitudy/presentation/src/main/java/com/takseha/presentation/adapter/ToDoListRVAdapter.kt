package com.takseha.presentation.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.mystudy.Commit
import com.takseha.data.dto.mystudy.MyStudyWithTodo
import com.takseha.data.dto.mystudy.Todo
import com.takseha.data.dto.mystudy.TodoStatus
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemMystudyBinding
import com.takseha.presentation.databinding.ItemTodoBinding
import com.takseha.presentation.ui.mystudy.MyStudyMainActivity
import java.time.LocalDate

class ToDoListRVAdapter(val context : Context, val todoList : List<Todo>) : RecyclerView.Adapter<ToDoListRVAdapter.ViewHolder>() {
    interface ItemClick {
        fun onClick(view: View, position: Int)
    }
    var itemClick: ItemClick? = null

    class ViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        var todoDate = binding.todoDate
        var todoTitle = binding.todoDetailTitle
        var todoTime = binding.todoTime
        var todoDetail = binding.todoDetailText
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
        holder.todoLinkBtn.setOnClickListener {
           // todoList[position].todoLink웹뷰로 이동
        }
        setCommitList(holder.commitList, todoList[position].commitList)

        // 클릭 이벤트 처리
        if (itemClick != null) {
            holder?.itemView?.setOnClickListener { v ->
                itemClick!!.onClick(v, position)
            }
        }
    }

    private fun setCommitList(commitListView: RecyclerView, commitList: List<Commit>) {
        val commitListRVAdapter = CommitListRVAdapter(context, commitList)

        commitListView.adapter = commitListRVAdapter
        commitListView.layoutManager = LinearLayoutManager(context)
    }

//    private fun clickCommitItem(commitListRVAdapter: CommitListRVAdapter, commitList: List<Commit>) {
//        commitListRVAdapter.itemClick = object : CommitListRVAdapter.ItemClick {
//            override fun onClick(view: View, position: Int) {
//
//            }
//        }
//    }

    override fun getItemCount(): Int {
        return todoList.size
    }
}