package com.takseha.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.takseha.data.dto.mystudy.StudyMember
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemMemberInRankBinding

class MemberRankRVAdapter(val context : Context, val studyMemberList : List<StudyMember>) : RecyclerView.Adapter<MemberRankRVAdapter.ViewHolder>() {
    interface ItemClick {
        fun onClick(view: View, position: Int)
    }
    var itemClick: ItemClick? = null

    class ViewHolder(val binding: ItemMemberInRankBinding) : RecyclerView.ViewHolder(binding.root) {
        val memberRank = binding.memberRank
        val profileImg = binding.profileImg
        val memberName = binding.memberName
        val memberScore = binding.memberScore
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMemberInRankBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setStudyMemberInfo(holder,position)

        // 클릭 이벤트 처리
        if (itemClick != null) {
            holder?.itemView?.setOnClickListener { v ->
                itemClick!!.onClick(v, position)
            }
        }
    }

    private fun setStudyMemberInfo(holder: ViewHolder, position: Int) {
        with(holder) {
            memberRank.text = (position + 1).toString()
            memberName.text = studyMemberList[position].userInfo.name
            memberScore.text = context.getString(R.string.study_member_score, (50 - (position + 1) * 2))
            Glide.with(context)
                .load(studyMemberList[position].userInfo.profileImageUrl)
                .error(R.drawable.logo_profile_default)
                .into(profileImg)
        }
    }

    override fun getItemCount(): Int {
        return studyMemberList.size
    }
}