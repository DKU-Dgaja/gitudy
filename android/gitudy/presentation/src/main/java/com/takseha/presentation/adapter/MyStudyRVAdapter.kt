package com.takseha.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.data.dto.mystudy.TodoStatus
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemMystudyBinding
import com.takseha.presentation.viewmodel.home.MyStudyWithTodo
import java.time.LocalDate

class MyStudyRVAdapter(val context : Context, val studyInfoList : List<MyStudyWithTodo>) : RecyclerView.Adapter<MyStudyRVAdapter.ViewHolder>() {
    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    class ViewHolder(val binding: ItemMystudyBinding) : RecyclerView.ViewHolder(binding.root) {
        val studyImg = binding.studyImg
        val studyName = binding.studyName
        val leaderTag = binding.leaderTag
        val teamScore = binding.studyScore
        val noTodoAlarm = binding.noTodoAlarm
        val todoTitle = binding.todoDetailTitle
        val todoCheck = binding.todoCheckText
        val todoTimeText = binding.todoTimeText
        val todoTime = binding.todoTime
        val todoCheckNum = binding.todoCheckNum
        val progressBar = binding.progressBar
        val studyEndTag = binding.studyEndTag
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMystudyBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setStudyInfo(holder, position)

        // 클릭 이벤트 처리
        if (itemClick != null) {
            holder?.itemView?.setOnClickListener { v ->
                itemClick!!.onClick(v, position)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setStudyInfo(holder: ViewHolder, position: Int) {
        val studyInfo = studyInfoList[position].studyInfo
        val urgentTodo = studyInfoList[position].urgentTodo!!

        if (studyInfo.status != StudyStatus.STUDY_INACTIVE) {
            val studyImage = setStudyImg(studyInfo.profileImageUrl.toIntOrNull() ?: 0)
            holder.studyImg.setImageResource(studyImage)
            holder.leaderTag.visibility = if (studyInfo.isLeader) VISIBLE else GONE
            holder.studyEndTag.visibility = GONE
            holder.todoCheckNum.text = context.getString(R.string.study_todo_check_num, urgentTodo.completeMemberCount, studyInfo.currentMember)
            holder.progressBar.progress = urgentTodo.completeMemberCount ?: 0
            holder.progressBar.max = studyInfo.currentMember
        } else {
            holder.studyImg.setImageResource(R.drawable.bg_mystudy_small_default)
            holder.leaderTag.visibility = GONE
            holder.studyEndTag.visibility = VISIBLE
            holder.todoCheckNum.text = context.getString(R.string.study_todo_check_num, 0, studyInfo.currentMember)
            holder.progressBar.progress = 0
            holder.progressBar.max = studyInfo.currentMember
            holder.noTodoAlarm.setTextColor(R.color.GS_300)
            holder.studyName.setTextColor(R.color.GS_300)
            holder.teamScore.setTextColor(R.color.GS_300)
        }
        holder.studyName.text = studyInfo.topic
        holder.teamScore.text = "${studyInfo.score}점"

        if (urgentTodo.todo == null) {
            holder.noTodoAlarm.visibility = VISIBLE
            holder.todoTitle.visibility = GONE
            holder.todoCheck.visibility = GONE
            holder.todoTimeText.visibility = GONE
            holder.todoTime.visibility = GONE
        } else {
            holder.noTodoAlarm.visibility = GONE
            holder.todoTitle.visibility = VISIBLE
            holder.todoCheck.visibility = VISIBLE
            holder.todoTimeText.visibility = VISIBLE
            holder.todoTime.visibility = VISIBLE
            holder.todoTitle.text = urgentTodo.todo!!.title
            holder.todoTime.text = urgentTodo.todo!!.todoDate

            if (urgentTodo.myStatus == TodoStatus.TODO_COMPLETE) {
                holder.todoCheck.text = "완료"
                holder.todoCheck.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.BASIC_BLUE
                    )
                )
            } else {
                holder.todoCheck.text = "미완료"
                if (urgentTodo.todo?.todoDate == LocalDate.now().toString()) {
                    holder.todoTime.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.BASIC_RED
                        )
                    )
                }
            }
        }
    }

    private fun setStudyImg(currentIdx: Int): Int {
        return when (currentIdx) {
            0 -> R.drawable.bg_mystudy_small_10
            1 -> R.drawable.bg_mystudy_small_9
            2 -> R.drawable.bg_mystudy_small_8
            3 -> R.drawable.bg_mystudy_small_7
            4 -> R.drawable.bg_mystudy_small_6
            5 -> R.drawable.bg_mystudy_small_5
            6 -> R.drawable.bg_mystudy_small_4
            7 -> R.drawable.bg_mystudy_small_3
            8 -> R.drawable.bg_mystudy_small_2
            else -> R.drawable.bg_mystudy_small_1
        }
    }

    override fun getItemCount(): Int {
        return studyInfoList.size
    }
}