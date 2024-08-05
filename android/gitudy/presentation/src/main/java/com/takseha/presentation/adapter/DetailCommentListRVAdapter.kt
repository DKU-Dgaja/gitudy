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
import com.takseha.data.dto.mystudy.StudyComment
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemCommentDetailBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class DetailCommentListRVAdapter(val context: Context, val commentList: List<StudyComment>) :
    RecyclerView.Adapter<DetailCommentListRVAdapter.ViewHolder>() {
    interface OnClickListener {
        fun onDeleteClick(view: View, position: Int)
        fun onLikeClick(view: View, position: Int)
        fun onHeartClick(view: View, position: Int)
    }

    var onClickListener: OnClickListener? = null

    class ViewHolder(val binding: ItemCommentDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        var profileImg = binding.profileImg
        var writerName = binding.writerName
        var date = binding.dateText
        var content = binding.contentText
        var moreBtn = binding.moreBtn
        var likeBtn = binding.likeBtn
        var heartBtn = binding.heartBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(commentList[position].userInfo.profileImageUrl)
            .error(R.drawable.logo_profile_default)
            .into(holder.profileImg)

        holder.writerName.text = commentList[position].userInfo.name
        holder.date.text =
            getCreatedDate(commentList[position].commentSetDate)
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
        holder.likeBtn.setOnClickListener { v ->
            this.onClickListener?.onLikeClick(v, position)
        }
        holder.heartBtn.setOnClickListener { v ->
            this.onClickListener?.onHeartClick(v, position)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCreatedDate(date: String): String {
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.getDefault())
        val localDateTime = LocalDateTime.parse(date)

        return localDateTime.format(dateFormat)
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