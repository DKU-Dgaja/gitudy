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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.takseha.data.dto.home.Notice
import com.takseha.data.dto.mystudy.StudyApplyMember
import com.takseha.data.dto.mystudy.StudyApplyMemberListResponse
import com.takseha.data.dto.mystudy.StudyComment
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemAlertBinding
import com.takseha.presentation.databinding.ItemApplyMemberBinding
import com.takseha.presentation.databinding.ItemCommentDetailBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class StudyApplyMemberListRVAdapter(val context: Context, val studyApplyMemberList: List<StudyApplyMember>) :
    RecyclerView.Adapter<StudyApplyMemberListRVAdapter.ViewHolder>() {
    interface OnClickListener {
        fun onClick(view: View, position: Int)
    }

    var onClickListener: OnClickListener? = null

    class ViewHolder(val binding: ItemApplyMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        var date = binding.dateText
        var memberProfileImg = binding.profileImg
        var memberName = binding.memberName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemApplyMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(studyApplyMemberList[position].profileImageUrl)
            .error(R.drawable.logo_profile_default)
            .into(holder.memberProfileImg)

        holder.date.text =
            getCreatedDate(studyApplyMemberList[position].createdDateTime)
        holder.memberName.text = studyApplyMemberList[position].name

        // 클릭 이벤트 처리
        if (onClickListener != null) {
            holder?.itemView?.setOnClickListener { v ->
                onClickListener!!.onClick(v, position)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCreatedDate(date: String): String {
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.getDefault())
        val localDateTime = LocalDateTime.parse(date)

        return localDateTime.format(dateFormat)
    }

    override fun getItemCount(): Int {
        return studyApplyMemberList.size
    }
}