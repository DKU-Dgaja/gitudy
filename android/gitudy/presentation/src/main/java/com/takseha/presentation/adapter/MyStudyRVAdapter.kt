package com.takseha.presentation.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.mystudy.MyStudyWithTodo
import com.takseha.data.dto.mystudy.TodoStatus
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemMystudyBinding
import java.time.LocalDate

class MyStudyRVAdapter(val context : Context, val studyInfoList : List<MyStudyWithTodo>) : RecyclerView.Adapter<MyStudyRVAdapter.ViewHolder>() {
    interface ItemClick {
        fun onClick(view: View, position: Int)
    }
    var itemClick: ItemClick? = null

    class ViewHolder(val binding: ItemMystudyBinding) : RecyclerView.ViewHolder(binding.root) {
        val studyImg = binding.studyImg
        val studyName = binding.studyName
        val teamScore = binding.studyScore
        val noTodoAlarm = binding.noTodoAlarm
        val todoTitle = binding.todoDetailTitle
        val todoCheck = binding.todoCheckText
        val todoTimeText = binding.todoTimeText
        val todoTime = binding.todoTime
        val todoCheckNum = binding.todoCheckNum
        val totalNum = binding.totalNum
        val progressBar = binding.progressBar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMystudyBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setBasicStudyInfo(holder,position)

        when (studyInfoList[position].todoCheck) {
            TodoStatus.TODO_EMPTY -> {
                holder.noTodoAlarm.visibility = VISIBLE
                holder.todoTitle.visibility = GONE
                holder.todoCheck.visibility = GONE
                holder.todoTimeText.visibility = GONE
                holder.todoTime.visibility = GONE
                holder.todoCheckNum.text = "0/"
                holder.totalNum.text = studyInfoList[position].studyInfo.currentMember.toString()
            }
            TodoStatus.TODO_INCOMPLETE -> {
                holder.noTodoAlarm.visibility = GONE
                holder.todoTitle.visibility = VISIBLE
                holder.todoCheck.visibility = VISIBLE
                holder.todoTimeText.visibility = VISIBLE
                holder.todoTime.visibility = VISIBLE
                holder.todoCheck.text = "미완료"
                holder.todoTitle.text = studyInfoList[position].todoTitle
                holder.todoTime.text = studyInfoList[position].todoTime
                holder.todoCheckNum.text = "${ studyInfoList[position].todoCheckNum }/"
                holder.totalNum.text = studyInfoList[position].studyInfo.currentMember.toString()

                if (studyInfoList[position].todoTime == LocalDate.now().toString()) {
                    holder.todoTime.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.BASIC_RED
                        )
                    )
                }
            }
            else -> {
                holder.noTodoAlarm.visibility = GONE
                holder.todoTitle.visibility = VISIBLE
                holder.todoCheck.visibility = VISIBLE
                holder.todoTimeText.visibility = VISIBLE
                holder.todoTime.visibility = VISIBLE
                holder.todoCheck.text = "완료"
                holder.todoTitle.text = studyInfoList[position].todoTitle
                holder.todoTime.text = studyInfoList[position].todoTime
                holder.todoCheckNum.text = "${ studyInfoList[position].todoCheckNum }/"
                holder.totalNum.text = studyInfoList[position].studyInfo.currentMember.toString()
            }
        }

        // 클릭 이벤트 처리
        if (itemClick != null) {
            holder?.itemView?.setOnClickListener { v ->
                itemClick!!.onClick(v, position)
            }
        }
    }

    private fun setBasicStudyInfo(holder: ViewHolder, position: Int) {
        holder.studyImg.setCardBackgroundColor(Color.parseColor(studyInfoList[position].studyImg))
        holder.studyName.text = studyInfoList[position].studyInfo.topic
        holder.teamScore.text = "${300 - studyInfoList[position].studyInfo.id * 10}점"
        holder.progressBar.progress = studyInfoList[position].todoCheckNum ?: 0
        holder.progressBar.max = studyInfoList[position].studyInfo.currentMember
    }

    override fun getItemCount(): Int {
        return studyInfoList.size
    }
}