package com.takseha.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.takseha.presentation.viewholder.MyStudyListViewHolder

// itemImageView.clipToOutline = true 이거 프로필 이미지 둥글게 할 때 사용
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