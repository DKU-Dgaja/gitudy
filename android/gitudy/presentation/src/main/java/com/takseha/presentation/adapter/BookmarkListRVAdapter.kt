package com.takseha.presentation.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.feed.StudyInfo
import com.takseha.data.dto.feed.StudyPeriodStatus
import com.takseha.data.dto.feed.UserInfo
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemBookmarkBinding
import com.takseha.presentation.databinding.ItemFeedBinding
import com.takseha.presentation.viewmodel.mystudy.BookmarkWithStatus
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class BookmarkListRVAdapter(val context : Context, val bookmarkList : List<BookmarkWithStatus>) : RecyclerView.Adapter<BookmarkListRVAdapter.ViewHolder>() {
    interface OnClickListener {
        fun onClick(view: View, position: Int)
        fun bookmarkClick(view: View, position: Int)
    }
    var onClickListener: OnClickListener? = null

    class ViewHolder(val binding: ItemBookmarkBinding) : RecyclerView.ViewHolder(binding.root) {
        val studyName = binding.studyName
        val bookmarkBtn = binding.bookmarkBtn
        val memberCount = binding.memberCount
        // val categoryList: RecyclerView = binding.categoryList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.studyName.text = bookmarkList[position].bookmarkInfo?.studyInfoWithIdResponse?.topic
        holder.memberCount.text = context.getString(R.string.feed_member_number, bookmarkList[position].bookmarkInfo?.studyInfoWithIdResponse?.currentMember, bookmarkList[position].bookmarkInfo?.studyInfoWithIdResponse?.maximumMember)
        holder.bookmarkBtn.setOnClickListener { v ->
            onClickListener!!.bookmarkClick(v, position)
        }

        // 스터디 클릭 이벤트 처리
        if (onClickListener != null) {
            holder.itemView.setOnClickListener { v ->
                onClickListener!!.onClick(v, position)
            }
        }
    }

    private fun setCommitRule(periodType: StudyPeriodStatus): String {
        when (periodType) {
            StudyPeriodStatus.STUDY_PERIOD_EVERYDAY -> return context.getString(R.string.feed_rule_everyday)
            StudyPeriodStatus.STUDY_PERIOD_WEEK -> return context.getString(R.string.feed_rule_week)
            StudyPeriodStatus.STUDY_PERIOD_NONE -> return context.getString(R.string.feed_rule_free)
        }
    }

    override fun getItemCount(): Int {
        return bookmarkList.size
    }
}