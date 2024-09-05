package com.takseha.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.mystudy.CommitStatus
import com.takseha.data.dto.profile.CommitWithStudyName
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemMyCommitBinding

class MyCommitListRVAdapter(val context : Context, val commitList : List<CommitWithStudyName>) : RecyclerView.Adapter<MyCommitListRVAdapter.ViewHolder>() {
    interface OnClickListener {
        fun onClick(view: View, position: Int)
    }
    var onClickListener: OnClickListener? = null

    class ViewHolder(val binding: ItemMyCommitBinding) : RecyclerView.ViewHolder(binding.root) {
        var commitTitle = binding.commitTitle
        var commitInfo = binding.commitInfo
        var commitStatus = binding.commitStatus
        val divider = binding.divider1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyCommitBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == itemCount - 1) holder.divider.visibility = GONE
        holder.commitTitle.text = commitList[position].commit?.message
        holder.commitInfo.text = context.getString(R.string.study_to_do_commit_info, commitList[position].studyName, commitList[position].commit?.commitDate)
        when (commitList[position].commit?.status) {
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
            else -> ""
        }

        if (onClickListener != null) {
            holder.itemView.setOnClickListener { v ->
                onClickListener!!.onClick(v, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return commitList.size
    }
}