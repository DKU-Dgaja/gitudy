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
import com.takseha.presentation.databinding.ItemFeedBinding
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class FeedRVAdapter(val context : Context, val studyInfoList : List<StudyInfo>, val studyCategoryMappingMap: Map<Int, List<String>>) : RecyclerView.Adapter<FeedRVAdapter.ViewHolder>() {
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
        val categoryList: RecyclerView = binding.categoryList
        val memberList: RecyclerView = binding.memberList
        val currentMember = binding.currentCnt
        val totalMember = binding.totalCnt
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.backgroundColor.setBackgroundColor(Color.parseColor(studyInfoList[position].profileImageUrl))
        holder.studyName.text = studyInfoList[position].topic
        holder.commitRule.text = setCommitRule(studyInfoList[position].periodType)
        holder.teamInfo.text = context.getString(R.string.study_team_rank_full, studyInfoList[position].id - 15, if (studyInfoList[position].lastCommitDay == null ) "없음" else studyInfoList[position].lastCommitDay)
        holder.teamScore.text = studyInfoList[position].score.toString()
        holder.totalDayCnt.text = context.getString(R.string.study_total_day_cnt, calculateTotalDayCnt(studyInfoList[position].createdDateTime))
        holder.currentMember.text = studyInfoList[position].currentMember.toString()
        holder.totalMember.text = context.getString(R.string.study_member_rv, studyInfoList[position].maximumMember)

        // holder.categoryList 구현
        setCategoryList(holder, position)
        // holder.memberList 구현
        setMemberList(holder, position)

        // 클릭 이벤트 처리
        if (itemClick != null) {
            holder?.itemView?.setOnClickListener { v ->
                itemClick!!.onClick(v, position)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateTotalDayCnt(createdDate: String): Long {
        val createdDateLocalDate = LocalDateTime.parse(createdDate)

        val nowDate = LocalDateTime.now()

        return ChronoUnit.DAYS.between(createdDateLocalDate, nowDate)
    }

    private fun setCategoryList(holder: ViewHolder, position: Int) {
        var categoryList = studyCategoryMappingMap[studyInfoList[position].id] ?: listOf()

        holder.categoryList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.categoryList.adapter = FeedCategoryRVAdapter(context, categoryList)
    }

    private fun setMemberList(holder: ViewHolder, position: Int) {
        val memberList: MutableList<UserInfo> = studyInfoList[position].userInfo.toMutableList()
        val emptyCnt = studyInfoList[position].maximumMember - studyInfoList[position].userInfo.size

        // 빈 프로필 추가
        for (i in 0 until emptyCnt) {
            memberList.add(UserInfo(-1, "empty", ""))
        }

        holder.memberList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.memberList.adapter = MemberListRVAdapter(context, memberList)
    }

    override fun getItemCount(): Int {
        return studyInfoList.size
    }
}