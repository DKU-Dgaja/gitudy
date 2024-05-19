package com.takseha.presentation.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.mystudy.MyStudyWithTodo
import com.takseha.presentation.databinding.ItemMystudyBinding

class MyStudyRVAdapter(val context : Context, val studyInfoList : List<MyStudyWithTodo>) : RecyclerView.Adapter<MyStudyRVAdapter.ViewHolder>() {
    private val backgroundColorList = listOf("#00BE93", "#00A19A", "#008291", "#08647A", "#386C5F", "#6E9B7B")

    class ViewHolder(val binding: ItemMystudyBinding) : RecyclerView.ViewHolder(binding.root) {
        val studyImg = binding.studyImg
        val studyName = binding.studyName
        val teamScore = binding.studyScore
        val todoTitle = binding.todoDetailTitle
        val todoCheck = binding.todoCheckText
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
        holder.studyImg.setCardBackgroundColor(Color.parseColor(backgroundColorList[position % 6]))
        holder.studyName.text = studyInfoList[position].studyInfo.topic
        holder.teamScore.text = "${studyInfoList[position].studyInfo.score.toString()}점"
        holder.todoTitle.text = studyInfoList[position].todoTitle
        holder.todoCheck.text = studyInfoList[position].todoCheck
        holder.todoTime.text = studyInfoList[position].todoTime
        holder.todoCheckNum.text = "${ studyInfoList[position].todoCheckNum }/"
        holder.totalNum.text = studyInfoList[position].studyInfo.maximumMember.toString()
        holder.progressBar.progress = studyInfoList[position].todoCheckNum
        holder.progressBar.max = studyInfoList[position].studyInfo.maximumMember
    }

    override fun getItemCount(): Int {
        return studyInfoList.size
    }
}