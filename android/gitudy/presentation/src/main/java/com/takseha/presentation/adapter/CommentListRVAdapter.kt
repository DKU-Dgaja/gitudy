package com.takseha.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
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

    class ViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        var profileImg = binding.profileImg
        var content = binding.contentText
        var date = binding.dateText
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

        holder.content.text = commentList[position].content
        holder.date.text =
            LocalDateTime.parse(commentList[position].commentSetDate).toLocalDate().toString()
    }

    override fun getItemCount(): Int {
        return commentList.size
    }
}