package com.takseha.presentation.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.study.StudyInfo
import com.takseha.presentation.R

class MyStudyListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(study: StudyInfo) {
        // myStudyListInfo binding 기능 구현하기
    }

    companion object {
        fun create(parent: ViewGroup) : MyStudyListViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mystudy, parent, false)

            return MyStudyListViewHolder(view)
        }
    }
}