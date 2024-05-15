package com.takseha.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.mystudy.StudyInfo
import com.takseha.presentation.databinding.ItemMystudyBinding

// itemImageView.clipToOutline = true 이거 프로필 이미지 둥글게 할 때 사용
class MyStudyRVAdapter(val context : Context, val studyInfoList : List<StudyInfo>) : RecyclerView.Adapter<MyStudyRVAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemMystudyBinding) : RecyclerView.ViewHolder(binding.root) {
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
        holder.studyName.text = studyInfoList[position].topic
        holder.teamScore.text = studyInfoList[position].score.toString()
    }

    override fun getItemCount(): Int {
        return studyInfoList.size
    }
}