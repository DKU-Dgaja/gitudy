package com.takseha.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.mystudy.Commit
import com.takseha.data.dto.mystudy.CommitStatus
import com.takseha.data.dto.mystudy.LikeCount
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemCommitBinding

class CommitListRVAdapter(val context : Context, val commitList : List<Commit>, val onClickListener: ToDoListRVAdapter.OnClickListener) : RecyclerView.Adapter<CommitListRVAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCommitBinding) : RecyclerView.ViewHolder(binding.root) {
        var commitTitle = binding.commitTitle
        var commitInfo = binding.commitInfo
        var commitStatus = binding.commitStatus

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val commit = Commit(
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
                    onClickListener.onCommitClick(commit)
                }
            }
        }
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
    }

    override fun getItemCount(): Int {
        return commitList.size
    }
}