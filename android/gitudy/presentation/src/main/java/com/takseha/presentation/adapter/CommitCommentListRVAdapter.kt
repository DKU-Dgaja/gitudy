package com.takseha.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.takseha.data.dto.mystudy.Comment
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemCommitCommentBinding
import com.takseha.presentation.ui.common.UTCToKoreanTimeConverter
import java.time.LocalDateTime

class CommitCommentListRVAdapter(val context: Context, val commentList: List<Comment>) :
    RecyclerView.Adapter<CommitCommentListRVAdapter.ViewHolder>() {
    interface OnClickListener {
        fun onDeleteClick(view: View, position: Int)
    }

    var onClickListener: OnClickListener? = null

    class ViewHolder(val binding: ItemCommitCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        var profileImg = binding.profileImg
        var writerName = binding.writerName
        var date = binding.dateText
        var content = binding.contentText
        var moreBtn = binding.moreBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommitCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(commentList[position].userInfo.profileImageUrl)
            .error(R.drawable.logo_profile_default)
            .into(holder.profileImg)

        holder.writerName.text = commentList[position].userInfo.name
        val localDateTime = LocalDateTime.parse(commentList[position].commentSetDate)
        holder.date.text = UTCToKoreanTimeConverter().convertToKoreaTime(localDateTime)
        holder.content.text = commentList[position].content

        if (commentList[position].isMyComment) {
            holder.moreBtn.visibility = VISIBLE
        } else {
            holder.moreBtn.visibility = GONE
        }

        holder.moreBtn.setOnClickListener { v ->
            // more 버튼 클릭 이벤트 처리
            showPopupMenu(v, position)
        }
    }

    private fun showPopupMenu(view: View, position: Int) {
        val popup = PopupMenu(context, view)
        popup.inflate(R.menu.delete_menu)

        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_delete -> {
                    // 삭제 버튼 클릭 처리
                    this.onClickListener?.onDeleteClick(view, position)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    override fun getItemCount(): Int {
        return commentList.size
    }
}