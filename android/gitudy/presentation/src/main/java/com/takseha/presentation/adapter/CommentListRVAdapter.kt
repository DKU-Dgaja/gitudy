package com.takseha.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.takseha.data.dto.mystudy.StudyComment
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemCommentBinding

class CommentListRVAdapter(val context: Context, val commentList: List<StudyComment>) : RecyclerView.Adapter<CommentListRVAdapter.ViewHolder>()  {
    interface OnClickListener {
        fun onUpdateClick(view: View, position: Int)
        fun onDeleteClick(view: View, position: Int)
    }
    var onClickListener: OnClickListener? = null

    class ViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        var profileImg = binding.profileImg
        var content = binding.contentText
        var date = binding.dateText
        var updateBtn = binding.updateBtn
        var deleteBtn = binding.deleteBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(commentList[position].userInfo.profileImageUrl)
            .error(R.drawable.logo_profile_default)
            .into(holder.profileImg)

        holder.content.text = commentList[position].content
        // holder.date.text = commentList[position].date
        holder.date.text = "2024-07-29"

        // TODO: 내 댓글인지 확인하고 수정 삭제 버튼 visibility 결정하는 로직 추가하기
        holder.updateBtn.visibility = GONE
        holder.deleteBtn.visibility = GONE

        holder.updateBtn.setOnClickListener { v ->
            this.onClickListener?.onUpdateClick(v, position)
        }
        holder.deleteBtn.setOnClickListener { v ->
            this.onClickListener?.onDeleteClick(v, position)
        }

    }

    override fun getItemCount(): Int {
        return commentList.size
    }
}