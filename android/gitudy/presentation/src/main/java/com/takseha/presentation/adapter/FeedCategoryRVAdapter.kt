package com.takseha.presentation.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.takseha.presentation.databinding.ItemCategoryBinding

class FeedCategoryRVAdapter(val context : Context, val categoryList: List<String>) : RecyclerView.Adapter<FeedCategoryRVAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        val categoryName = binding.categoryName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.categoryName.text = categoryList[position]
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}