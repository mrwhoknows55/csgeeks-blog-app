package com.mrwhoknows.csgeeks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.model.ArticleList
import com.mrwhoknows.csgeeks.util.StringFormatter

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
        private val author: TextView = itemView.findViewById(R.id.tvAuthorNameItem)
        private val createdAt: TextView = itemView.findViewById(R.id.tvArticleDateItem)
        private val desc: TextView = itemView.findViewById(R.id.tvDesc)
        private val thmb: ImageView = itemView.findViewById(R.id.ivThumbnailItem)

        fun bind(data: ArticleList.Article) {
            title.text = data.title
            desc.text = data.description
            author.text = "Created by, ${data.author}"

            val date = StringFormatter.convertDateTimeToString(
                data.created,
                "yyyy-MM-dd'T'HH:mm:ss.SSS+00:00",
                "dd, MMM yyyy hh:mm a"
            )
            createdAt.text = "at  $date"

            Glide.with(itemView.context).load(data.thumbnail).into(thmb)
        }
    }

    private var onItemClickListener: ((ArticleList.Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (ArticleList.Article) -> Unit) {
        onItemClickListener = listener
    }
}