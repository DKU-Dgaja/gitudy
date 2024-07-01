package com.takseha.presentation.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.feed.StudyPeriod
import com.takseha.data.dto.feed.StudyInfo
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemFeedBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class FeedRVAdapter(val context : Context, val studyInfoList : List<StudyInfo>) : RecyclerView.Adapter<FeedRVAdapter.ViewHolder>() {
    private val backgroundColorList = listOf("#00BE93", "#00A19A", "#008291", "#08647A", "#386C5F", "#6E9B7B")

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }
    var itemClick: ItemClick? = null

    class ViewHolder(val binding: ItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {
        val backgroundColor = binding.studyInfoLayout
        val studyName = binding.studyName
        val commitRule = binding.commitRule
        val teamInfo = binding.teamRankAndRecentInfo
        val teamScore = binding.teamScore
        val totalDayCnt = binding.totalDayCnt
        val currentMember = binding.currentCnt
        val totalMember = binding.totalCnt
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (studyInfoList[position].profileImageUrl.isEmpty()) {
            holder.backgroundColor.setBackgroundColor(Color.parseColor(backgroundColorList[position % 6]))
        } else if (studyInfoList[position].profileImageUrl != "string") {
            holder.backgroundColor.setBackgroundColor(Color.parseColor(studyInfoList[position].profileImageUrl))
        } else {
            holder.backgroundColor.setBackgroundColor(Color.parseColor(backgroundColorList[position % 6]))
        }
        holder.studyName.text = studyInfoList[position].topic
        holder.commitRule.text = setCommitRule(studyInfoList[position].periodType)
        holder.teamInfo.text = context.getString(R.string.study_team_rank_full, studyInfoList[position].id - 15, if (studyInfoList[position].lastCommitDay == null ) "없음" else studyInfoList[position].lastCommitDay)
        holder.teamScore.text = (300 - studyInfoList[position].id * 10).toString()
        holder.totalDayCnt.text = context.getString(R.string.study_total_day_cnt, calculateTotalDayCnt(studyInfoList[position].createdDateTime))
        holder.currentMember.text = studyInfoList[position].currentMember.toString()
        holder.totalMember.text = context.getString(R.string.study_member_rv, studyInfoList[position].maximumMember)

        // 클릭 이벤트 처리
        if (itemClick != null) {
            holder?.itemView?.setOnClickListener { v ->
                itemClick!!.onClick(v, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return studyInfoList.size
    }

    private fun setCommitRule(periodType: StudyPeriod): String {
        when (periodType) {
            StudyPeriod.STUDY_PERIOD_EVERYDAY -> return context.getString(R.string.feed_rule_everyday)
            StudyPeriod.STUDY_PERIOD_WEEK -> return context.getString(R.string.feed_rule_week)
            StudyPeriod.STUDY_PERIOD_NONE -> return context.getString(R.string.feed_rule_free)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateTotalDayCnt(createdDate: String): Long {
        val createdDateLocalDate = LocalDateTime.parse(createdDate)

        val nowDate = LocalDateTime.now()

        return ChronoUnit.DAYS.between(createdDateLocalDate, nowDate)
    }
}