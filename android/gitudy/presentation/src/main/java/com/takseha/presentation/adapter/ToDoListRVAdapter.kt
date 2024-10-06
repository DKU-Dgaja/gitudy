package com.takseha.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
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
import com.takseha.presentation.ui.common.UTCToKoreanTimeConverter
import java.time.LocalDate
import java.time.LocalDateTime

class ToDoListRVAdapter(val context: Context, val todoList: List<Todo>, val isLeader: Boolean?) :
    RecyclerView.Adapter<ToDoListRVAdapter.ViewHolder>() {
    interface OnClickListener {
        fun onCommitClick(commit: Commit)
        fun onDeleteClick(view: View, position: Int)
        fun onLinkClick(view: View, position: Int)
    }

    var onClickListener: OnClickListener? = null

    class ViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        var todoDate = binding.todoDate
        var todoTitle = binding.todoDetailTitle
        var todoSetDate = binding.todoSetDate
        var todoDetail = binding.todoDetailText
        var todoLinkBtn = binding.todoLinkBtn
        var moreBtn = binding.moreBtn
        var commitList: RecyclerView = binding.commitList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val localDateTime = LocalDateTime.parse(todoList[position].todoSetDate)
        holder.todoSetDate.text = UTCToKoreanTimeConverter().convertToKoreaDate(localDateTime)
        holder.todoTitle.text = todoList[position].title
        holder.todoDate.text = todoList[position].todoDate
        holder.todoDetail.text = todoList[position].detail
        setCommitList(holder, position)

        if (todoList[position].todoDate == LocalDate.now().toString()) {
            holder.todoDate.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.BASIC_RED
                )
            )
        }

        if (isLeader == true) {
            holder.moreBtn.visibility = VISIBLE
        } else {
            holder.moreBtn.visibility = GONE
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

    private fun setCommitList(holder: ViewHolder, position: Int) {
        holder.commitList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        holder.commitList.adapter =
            CommitListRVAdapter(context, todoList[position].commitList, onClickListener!!)
    }

    private fun showPopupMenu(view: View, position: Int) {
        val popup = PopupMenu(context, view)
        popup.inflate(R.menu.delete_menu)

        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
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