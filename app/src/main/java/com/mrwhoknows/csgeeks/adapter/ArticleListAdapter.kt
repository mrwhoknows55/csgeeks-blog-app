package com.mrwhoknows.csgeeks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.model.ArticleList

class ArticleListAdapter(private val articleMetaList: ArticleList) :
    RecyclerView.Adapter<ArticleListAdapter.ArticleListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleListViewHolder {
        return ArticleListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_post_meta_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ArticleListViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onItemClickListener?.let {
                it(articleMetaList.articles[position])
            }
        }
        holder.bind(articleMetaList.articles[position])
    }

    override fun getItemCount() = articleMetaList.articles.size

    inner class ArticleListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tvArticleItemTitle)
        private val author: TextView = itemView.findViewById(R.id.tvAutherNameItem)
        fun bind(data: ArticleList.Article) {
            title.text = data.title
            author.text = "Created by, ${data.author}"
        }
    }

    private var onItemClickListener: ((ArticleList.Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (ArticleList.Article) -> Unit) {
        onItemClickListener = listener
    }
}