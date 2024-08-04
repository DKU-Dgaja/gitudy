package com.takseha.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.takseha.data.dto.mystudy.StudyComment
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemCommentBinding
import java.time.LocalDateTime

class CommentListRVAdapter(val context: Context, val commentList: List<StudyComment>) :
    RecyclerView.Adapter<CommentListRVAdapter.ViewHolder>() {
    interface OnClickListener {
        fun onUpdateClick(view: View, position: Int)
        fun onConfirmClick(view: View, position: Int)
        fun onDeleteClick(view: View, position: Int)
    }

    var onClickListener: OnClickListener? = null

    class ViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        var profileImg = binding.profileImg
        var content = binding.contentText
        var editContent = binding.contentEditText
        var date = binding.dateText
        var updateBtn = binding.updateBtn
        var confirmBtn = binding.confirmBtn
        var deleteBtn = binding.deleteBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(commentList[position].userInfo.profileImageUrl)
            .error(R.drawable.logo_profile_default)
            .into(holder.profileImg)

        holder.editContent.visibility = GONE
        holder.content.text = commentList[position].content
        holder.date.text =
            LocalDateTime.parse(commentList[position].commentSetDate).toLocalDate().toString()

        if (commentList[position].isMyComment) {
            holder.updateBtn.visibility = VISIBLE
            holder.deleteBtn.visibility = VISIBLE
        } else {
            holder.updateBtn.visibility = GONE
            holder.deleteBtn.visibility = GONE
        }

        holder.updateBtn.setOnClickListener { v ->
            holder.content.visibility = GONE
            holder.updateBtn.visibility = GONE
            holder.confirmBtn.visibility = VISIBLE
            holder.editContent.visibility = VISIBLE
            holder.editContent.setText(commentList[position].content)
            this.onClickListener?.onUpdateClick(v, position)
        }

        holder.confirmBtn.setOnClickListener { v ->
            this.onClickListener?.onConfirmClick(v, position)
            holder.content.visibility = VISIBLE
            holder.updateBtn.visibility = VISIBLE
            holder.confirmBtn.visibility = GONE
            holder.editContent.visibility = GONE
        }

        holder.deleteBtn.setOnClickListener { v ->
            this.onClickListener?.onDeleteClick(v, position)
        }

    }

    override fun getItemCount(): Int {
        return commentList.size
    }
}