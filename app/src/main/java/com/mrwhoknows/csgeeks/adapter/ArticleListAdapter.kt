package com.mrwhoknows.csgeeks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.model.PostMeta

class ArticleListAdapter(private val articleMetaList: List<PostMeta.Post>) :
    RecyclerView.Adapter<ArticleListAdapter.ArticleListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleListViewHolder {
        return ArticleListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_post_meta_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ArticleListViewHolder, position: Int) {
        holder.bind(articleMetaList[position])
    }

    override fun getItemCount() = articleMetaList.size

    inner class ArticleListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tvArticleItemTitle)
        private val author: TextView = itemView.findViewById(R.id.tvAutherNameItem)
        fun bind(data: PostMeta.Post) {
            title.text = data.title
            author.text = "Created by, ${data.author}"
        }
    }
}