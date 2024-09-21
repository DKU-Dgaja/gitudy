package com.takseha.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.takseha.data.dto.mystudy.Comment
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemCommentBinding
import com.takseha.presentation.ui.common.UTCToKoreanTimeConverter
import java.time.LocalDateTime

class CommentListRVAdapter(val context: Context, val commentList: List<Comment>) :
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
        val localDateTime = LocalDateTime.parse(commentList[position].commentSetDate)
        holder.date.text = UTCToKoreanTimeConverter().convertToKoreaDate(localDateTime)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }
}