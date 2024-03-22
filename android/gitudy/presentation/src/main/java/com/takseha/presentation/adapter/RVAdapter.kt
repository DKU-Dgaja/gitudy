package com.takseha.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.takseha.presentation.viewholder.MyStudyListViewHolder

class RVAdapter() : RecyclerView.Adapter<MyStudyListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyStudyListViewHolder {
        return MyStudyListViewHolder.create(parent)
    }

    override fun getItemCount(): Int {
        // adapter 관련 기능 구현하기
        return 1
    }

    override fun onBindViewHolder(holder: MyStudyListViewHolder, position: Int) {
        // adapter 관련 기능 구현하기
    }
}