package com.takseha.presentation.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.takseha.data.dto.feed.Category
import com.takseha.presentation.R
import com.takseha.presentation.databinding.ItemCategoryAllBinding

class AllCategoryRVAdapter(
    private val context: Context,
    private val categoryList: List<Category>
) : RecyclerView.Adapter<AllCategoryRVAdapter.ViewHolder>() {

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }
    var itemClick: ItemClick? = null

    private val selectedItemIds = mutableSetOf<Int>()

    class ViewHolder(val binding: ItemCategoryAllBinding) : RecyclerView.ViewHolder(binding.root) {
        val categoryName = binding.categoryName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryAllBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.categoryName.text = categoryList[position].name

        if (selectedItemIds.contains(categoryList[position].id)) {
            holder.categoryName.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.BASIC_GREEN))
            holder.categoryName.setTextColor(ContextCompat.getColor(context, R.color.BLACK))
        } else {
            holder.categoryName.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F4F5F5"))
            holder.categoryName.setTextColor(ContextCompat.getColor(context, R.color.GS_400))
        }

        // 클릭 이벤트 처리
        holder.itemView.setOnClickListener { v ->
            itemClick?.onClick(v, position)
            toggleItemSelected(categoryList[position].id)
            notifyItemChanged(position)
        }
    }

    private fun toggleItemSelected(categoryId: Int) {
        if (selectedItemIds.contains(categoryId)) {
            selectedItemIds.remove(categoryId)
        } else {
            selectedItemIds.add(categoryId)
        }
    }

    fun getSelectedItems(): List<Int> {
        return selectedItemIds.toList()
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}
