package com.takseha.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.mystudy.TodoStatus
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemMainStudyBinding
import com.takseha.presentation.viewmodel.home.MyStudyWithTodo
import java.time.LocalDate

class MainStudyRVAdapter(val context: Context, val studyInfoList: List<MyStudyWithTodo>) :
    RecyclerView.Adapter<MainStudyRVAdapter.ViewHolder>() {
    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    class ViewHolder(val binding: ItemMainStudyBinding) : RecyclerView.ViewHolder(binding.root) {
        val studyImg = binding.studyImg
        val studyName = binding.studyName
        val todoTitle = binding.todoDetailTitle
        val todoCheck = binding.todoCheckText
        val todoTime = binding.todoTime
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMainStudyBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setStudyInfo(holder, position)

        // 클릭 이벤트 처리
        if (itemClick != null) {
            holder.itemView.setOnClickListener { v ->
                itemClick!!.onClick(v, position)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setStudyInfo(holder: ViewHolder, position: Int) {
        val studyInfo = studyInfoList[position].studyInfo
        val urgentTodo = studyInfoList[position].urgentTodo!!
        val studyImage = setStudyImg(studyInfo.profileImageUrl.toIntOrNull() ?: 0)

        holder.studyImg.setImageResource(studyImage)
        holder.studyName.text = studyInfo.topic
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