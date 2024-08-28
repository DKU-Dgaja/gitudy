package com.takseha.presentation.adapter

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.feed.StudyPeriodStatus
import com.takseha.data.dto.feed.UserInfo
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemFeedBinding
import com.takseha.presentation.ui.common.UTCToKoreanTimeConverter
import com.takseha.presentation.viewmodel.feed.StudyInfoWithBookmarkStatus
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class FeedRVAdapter(
    val context: Context,
    val studyInfoList: List<StudyInfoWithBookmarkStatus>,
    val studyCategoryMappingMap: Map<Int, List<String>>
) : RecyclerView.Adapter<FeedRVAdapter.ViewHolder>() {
    interface OnClickListener {
        fun onClick(view: View, position: Int)
        fun bookmarkClick(view: View, position: Int)
    }

    var onClickListener: OnClickListener? = null

    class ViewHolder(val binding: ItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {
        val studyImg = binding.studyImg
        val studyName = binding.studyName
        val bookmarkBtn = binding.bookmarkBtn
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
        bindFull(holder, position)
    }

    // payload가 있을 때
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            // payload가 없으면 전체 업데이트를 호출
            bindFull(holder, position)
        } else {
            for (payload in payloads) {
                // TODO: 나중에 로직 제대로 변경하기!
                if (payload == "bookmark") {
                    Log.d("FeedRVAdapter", studyInfoList[position].isMyBookmark.toString())
                    if (holder.bookmarkBtn.drawable.constantState == ContextCompat.getDrawable(context, R.drawable.ic_feed_save_green)!!.constantState) {
                        holder.bookmarkBtn.setImageResource(R.drawable.ic_feed_save_white)
                    } else {
                        holder.bookmarkBtn.setImageResource(R.drawable.ic_feed_save_green)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindFull(holder: ViewHolder, position: Int) {
        val studyImage = setStudyImg(studyInfoList[position].studyInfo.profileImageUrl.toIntOrNull() ?: 0)

        holder.studyImg.setImageResource(studyImage)
        holder.studyName.text = studyInfoList[position].studyInfo.topic
        holder.commitRule.text = setCommitRule(studyInfoList[position].studyInfo.periodType)
        holder.teamInfo.text = context.getString(R.string.study_team_rank_full, studyInfoList[position].rank,
            studyInfoList[position].studyInfo.lastCommitDay
        )
        holder.teamScore.text = studyInfoList[position].studyInfo.score.toString()
        holder.totalDayCnt.text = context.getString(R.string.study_total_day_cnt, calculateTotalDayCnt(studyInfoList[position].studyInfo.createdDateTime))
        holder.currentMember.text = studyInfoList[position].studyInfo.currentMember.toString()
        holder.totalMember.text = context.getString(R.string.study_member_rv, studyInfoList[position].studyInfo.maximumMember)
        if (studyInfoList[position].isMyBookmark) {
            holder.bookmarkBtn.setImageResource(R.drawable.ic_feed_save_green)
        } else {
            holder.bookmarkBtn.setImageResource(R.drawable.ic_feed_save_white)
        }

        // Category 및 Member 리스트 설정
        setCategoryList(holder, position)
        setMemberList(holder, position)

        // 북마크 버튼 클릭 이벤트 처리
        holder.bookmarkBtn.setOnClickListener { v ->
            onClickListener?.bookmarkClick(v, position)
            notifyItemChanged(position, "bookmark")
        }

        // 스터디 클릭 이벤트 처리
        holder.itemView.setOnClickListener { v ->
            onClickListener?.onClick(v, position)
        }
    }

    private fun setStudyImg(currentIdx: Int): Int {
        return when (currentIdx) {
            0 -> R.drawable.bg_feed_full_10
            1 -> R.drawable.bg_feed_full_9
            2 -> R.drawable.bg_feed_full_8
            3 -> R.drawable.bg_feed_full_7
            4 -> R.drawable.bg_feed_full_6
            5 -> R.drawable.bg_feed_full_5
            6 -> R.drawable.bg_feed_full_4
            7 -> R.drawable.bg_feed_full_3
            8 -> R.drawable.bg_feed_full_2
            else -> R.drawable.bg_feed_full_1
        }
    }

    private fun setCommitRule(periodType: StudyPeriodStatus): String {
        return when (periodType) {
            StudyPeriodStatus.STUDY_PERIOD_EVERYDAY -> context.getString(R.string.feed_rule_everyday)
            StudyPeriodStatus.STUDY_PERIOD_WEEK -> context.getString(R.string.feed_rule_week)
            StudyPeriodStatus.STUDY_PERIOD_NONE -> context.getString(R.string.feed_rule_free)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateTotalDayCnt(createdDate: String): Long {
        val createdDateLocalDate = LocalDateTime.parse(createdDate)
        val utcZonedDateTime = createdDateLocalDate.atZone(ZoneId.of("UTC"))
        val koreaZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"))
        val nowDate = LocalDateTime.now()

        return ChronoUnit.DAYS.between(koreaZonedDateTime, nowDate)
    }

    private fun setCategoryList(holder: ViewHolder, position: Int) {
        var categoryList = studyCategoryMappingMap[studyInfoList[position].studyInfo.id] ?: listOf()

        holder.categoryList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.categoryList.adapter = FeedCategoryRVAdapter(context, categoryList)
    }

    private fun setMemberList(holder: ViewHolder, position: Int) {
        val memberList: MutableList<UserInfo> =
            studyInfoList[position].studyInfo.userInfo.toMutableList()
        val emptyCnt =
            studyInfoList[position].studyInfo.maximumMember - studyInfoList[position].studyInfo.userInfo.size

        // 빈 프로필 추가
        for (i in 0 until emptyCnt) {
            memberList.add(UserInfo(-1, "empty", ""))
        }

        holder.memberList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.memberList.adapter = MemberListRVAdapter(context, memberList)
    }

    override fun getItemCount(): Int {
        return studyInfoList.size
    }
}