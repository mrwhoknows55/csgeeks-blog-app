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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

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
        private val desc: TextView = itemView.findViewById(R.id.tvDesc)

        //TODO add time
        private val thmb: ImageView = itemView.findViewById(R.id.ivThubm)
        fun bind(data: ArticleList.Article) {
            title.text = data.title
            desc.text = data.description
            Glide.with(itemView.context).load(data.thumbnail).into(thmb)

            val inputDateFormatter =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+00:00", Locale.getDefault())
                // SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            inputDateFormatter.timeZone = TimeZone.getTimeZone("UTC")

            val outputDateFormatter =
                SimpleDateFormat("dd, MMM yyyy hh:mm a", Locale.getDefault())
            outputDateFormatter.timeZone = TimeZone.getDefault()

            val dateTime = inputDateFormatter.parse(data.created)
            val date = outputDateFormatter.format(dateTime!!)

            author.text = "Created by, ${data.author} at  $date"
        }
    }

    private var onItemClickListener: ((ArticleList.Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (ArticleList.Article) -> Unit) {
        onItemClickListener = listener
    }
}