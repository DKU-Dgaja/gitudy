package com.takseha.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.home.Notice
import com.takseha.presentation.databinding.ItemAlertBinding
import com.takseha.presentation.ui.common.UTCToKoreanTimeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class NoticeListRVAdapter(val context: Context, val noticeList: List<Notice>) :
    RecyclerView.Adapter<NoticeListRVAdapter.ViewHolder>() {
    interface OnClickListener {
        fun onClick(view: View, position: Int)
    }

    var onClickListener: OnClickListener? = null

    class ViewHolder(val binding: ItemAlertBinding) : RecyclerView.ViewHolder(binding.root) {
        var date = binding.dateText
        var title = binding.alertTitle
        var content = binding.alertContent
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlertBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val localDateTime = LocalDateTime.parse(noticeList[position].localDateTime)
        holder.date.text = UTCToKoreanTimeConverter().convertToKoreaTime(localDateTime)
        holder.title.text = noticeList[position].title
        holder.content.text = noticeList[position].message

        // 클릭 이벤트 처리
        if (onClickListener != null) {
            holder?.itemView?.setOnClickListener { v ->
                onClickListener!!.onClick(v, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return noticeList.size
    }
}