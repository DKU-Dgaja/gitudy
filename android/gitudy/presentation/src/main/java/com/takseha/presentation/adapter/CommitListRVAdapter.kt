package com.takseha.presentation.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.mystudy.Commit
import com.takseha.data.dto.mystudy.CommitStatus
import com.takseha.data.dto.mystudy.MyStudyWithTodo
import com.takseha.data.dto.mystudy.Todo
import com.takseha.data.dto.mystudy.TodoStatus
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemCommitBinding
import com.takseha.presentation.databinding.ItemMystudyBinding
import com.takseha.presentation.databinding.ItemTodoBinding
import java.time.LocalDate

class CommitListRVAdapter(val context : Context, val commitList : List<Commit>) : RecyclerView.Adapter<CommitListRVAdapter.ViewHolder>() {
    interface ItemClick {
        fun onClick(view: View, position: Int)
    }
    var itemClick: ItemClick? = null

    class ViewHolder(val binding: ItemCommitBinding) : RecyclerView.ViewHolder(binding.root) {
        var commitTitle = binding.commitTitle
        var commitInfo = binding.commitInfo
        var commitStatus = binding.commitStatus
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommitBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.commitTitle.text = commitList[position].message
        holder.commitInfo.text = context.getString(R.string.study_to_do_commit_info, commitList[position].rejectionReason, commitList[position].commitDate)
        when (commitList[position].status) {
            CommitStatus.COMMIT_APPROVAL -> holder.commitStatus.text = "승인완료"
            CommitStatus.COMMIT_DELETE -> {
                holder.commitStatus.text = "커밋삭제"
                holder.commitStatus.setTextColor(ContextCompat.getColor(context, R.color.GS_500))
            }
            CommitStatus.COMMIT_REJECTION -> {
                holder.commitStatus.text = "승인반려"
                holder.commitStatus.setTextColor(ContextCompat.getColor(context, R.color.BASIC_RED))
            }
            CommitStatus.COMMIT_WAITING -> {
                holder.commitStatus.text = "승인대기"
                holder.commitStatus.setTextColor(ContextCompat.getColor(context, R.color.BASIC_GREEN))
            }
        }

        // 클릭 이벤트 처리
        if (itemClick != null) {
            holder?.itemView?.setOnClickListener { v ->
                itemClick!!.onClick(v, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return commitList.size
    }
}