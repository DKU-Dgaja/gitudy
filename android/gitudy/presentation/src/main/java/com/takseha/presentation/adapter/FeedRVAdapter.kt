package com.takseha.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.feed.StudyPeriod
import com.takseha.data.dto.mystudy.StudyInfo
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemFeedBinding
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// itemImageView.clipToOutline = true 이거 프로필 이미지 둥글게 할 때 사용
class FeedRVAdapter(val context : Context, val studyInfoList : List<StudyInfo>) : RecyclerView.Adapter<FeedRVAdapter.ViewHolder>() {


    class ViewHolder(val binding: ItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {
        val studyName = binding.studyName
        val commitRule = binding.commitRule
        val teamInfo = binding.teamRankAndRecentInfo
        val teamScore = binding.teamScore
        val totalDayCnt = binding.totalDayCnt
        val currentMember = binding.currentCnt
        val totalMember = binding.totalCnt
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedRVAdapter.ViewHolder {
        val binding = ItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: FeedRVAdapter.ViewHolder, position: Int) {
        holder.studyName.text = studyInfoList[position].topic
        holder.commitRule.text = setCommitRule(studyInfoList[position].periodType)
        holder.teamInfo.text = context.getString(R.string.study_team_rank_full, position, studyInfoList[position].lastCommitDay)
        holder.teamScore.text = studyInfoList[position].score.toString()
        holder.totalDayCnt.text = context.getString(R.string.study_total_day_cnt, calculateTotalDayCnt(studyInfoList[position].createdDateTime.toLocalDate()))
        holder.currentMember.text = studyInfoList[position].currentMember.toString()
        holder.totalMember.text = studyInfoList[position].maximumMember.toString()
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
    private fun calculateTotalDayCnt(createdDate: LocalDate): Long {
        val nowDate = LocalDate.now()

        return ChronoUnit.DAYS.between(nowDate, createdDate)
    }
}