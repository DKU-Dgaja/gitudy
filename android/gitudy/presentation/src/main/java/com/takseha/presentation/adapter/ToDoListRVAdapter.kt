package com.takseha.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.mystudy.Commit
import com.takseha.data.dto.mystudy.Todo
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemTodoBinding
import java.time.LocalDate
import java.time.LocalDateTime

class ToDoListRVAdapter(val context: Context, val todoList: List<Todo>) : RecyclerView.Adapter<ToDoListRVAdapter.ViewHolder>() {
    interface OnClickListener {
        fun onCommitClick(commit: Commit)
        fun onUpdateClick(view: View, position: Int)
        fun onDeleteClick(view: View, position: Int)
        fun onLinkClick(view: View, position: Int)
    }
    var onClickListener: OnClickListener? = null

    class ViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        var todoDate = binding.todoDate
        var todoTitle = binding.todoDetailTitle
        var todoSetDate = binding.todoSetDate
        var todoDetail = binding.todoDetailText
        var todoCode = binding.todoCode
        var todoLinkBtn = binding.todoLinkBtn
        var moreBtn = binding.moreBtn
        var commitList = binding.commitList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.todoSetDate.text = LocalDateTime.parse(todoList[position].todoSetDate).toLocalDate().toString()
        holder.todoTitle.text = todoList[position].title
        holder.todoDate.text = todoList[position].todoDate
        holder.todoDetail.text = todoList[position].detail
        holder.todoCode.text = todoList[position].todoCode
        setCommitList(holder.commitList, todoList[position].commitList)

        if (todoList[position].todoDate == LocalDate.now().toString()) {
            holder.todoDate.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.BASIC_RED
                )
            )
        }

        holder.moreBtn.setOnClickListener { v ->
            // more 버튼 클릭 이벤트 처리
            showPopupMenu(v, position)
        }
        holder.todoLinkBtn.setOnClickListener { v ->
            // todoList[position].todoLink웹뷰로 이동
            this.onClickListener?.onLinkClick(v, position)
        }
    }

    private fun setCommitList(commitListView: RecyclerView, commitList: List<Commit>) {
        val commitListRVAdapter = CommitListRVAdapter(context, commitList, this.onClickListener!!)

        commitListView.adapter = commitListRVAdapter
        commitListView.layoutManager = LinearLayoutManager(context)
    }

    private fun showPopupMenu(view: View, position: Int) {
        val popup = PopupMenu(context, view)
        popup.inflate(R.menu.todo_item_menu)

        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_edit -> {
                    // 수정 버튼 클릭 처리
                    this.onClickListener?.onUpdateClick(view, position)
                    true
                }
                R.id.menu_delete -> {
                    // 삭제 버튼 클릭 처리
                    this.onClickListener?.onDeleteClick(view, position)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }


    override fun getItemCount(): Int {
        return todoList.size
    }
}