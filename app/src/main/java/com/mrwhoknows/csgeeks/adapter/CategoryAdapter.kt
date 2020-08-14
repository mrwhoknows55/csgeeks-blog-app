package com.mrwhoknows.csgeeks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.model.ArticleTags
import kotlinx.android.synthetic.main.item_categories.view.*

class CategoryAdapter(private val tags: ArticleTags) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(tag: String) {
            itemView.tvCategoryName.text = tag
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder =
        CategoryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_categories, parent, false)
        )

    override fun getItemCount(): Int = tags.tags.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(tags.tags[position])
    }
}