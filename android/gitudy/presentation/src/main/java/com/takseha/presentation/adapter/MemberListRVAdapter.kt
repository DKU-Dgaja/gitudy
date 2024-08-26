package com.takseha.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.takseha.data.dto.feed.UserInfo
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemMemberInStudyListBinding

class MemberListRVAdapter(val context : Context, val userInfoList : List<UserInfo>) : RecyclerView.Adapter<MemberListRVAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemMemberInStudyListBinding) : RecyclerView.ViewHolder(binding.root) {
        val profileImg = binding.profileImg
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMemberInStudyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (userInfoList[position].id == -1) {
            holder.profileImg.setImageResource(R.drawable.ic_member_list_empty)
        } else {
            Glide.with(context)
                .load(userInfoList[position].profileImageUrl)
                .error(R.drawable.ic_member_list_empty)
                .into(holder.profileImg)
        }
    }

    override fun getItemCount(): Int {
        return userInfoList.size
    }
}