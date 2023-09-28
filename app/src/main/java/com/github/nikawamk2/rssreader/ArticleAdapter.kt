package com.github.nikawamk2.rssreader

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ArticleAdapter(private val context: Context, private val articleList: ArrayList<ArticleInfo>) : BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    override fun getCount(): Int {
        return articleList.size
    }

    override fun getItem(position: Int): Any {
        return articleList[position]
    }

    override fun getItemId(position: Int): Long {
        return articleList[position].itemId
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = articleList[position];

        var currentView = convertView
        if(convertView == null){
            currentView = inflater.inflate(R.layout.row_article, null);
        }

        if (currentView == null) {
            return View(context);
        }

        currentView.findViewById<TextView>(R.id.article_id).text = item.articleId
        currentView.findViewById<TextView>(R.id.article_url).text = item.articleUrl
        currentView.findViewById<TextView>(R.id.article_name).text = item.articleName

        return currentView
    }
}