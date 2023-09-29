package com.github.nikawamk2.rssreader

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.github.nikawamk2.rssreader.models.RssFeedInfo

class RssFeedAdapter(private val context: Context, private val rssFeedList: ArrayList<RssFeedInfo>) : BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    override fun getCount(): Int {
        return rssFeedList.size
    }

    override fun getItem(position: Int): Any {
        return rssFeedList[position]
    }

    override fun getItemId(position: Int): Long {
        return rssFeedList[position].itemId
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = rssFeedList[position];

        var currentView = convertView
        if(convertView == null){
            currentView = inflater.inflate(R.layout.row_rss_feed, null);
        }

        if (currentView == null) {
            return View(context);
        }

        currentView.findViewById<TextView>(R.id.feed_id).text = item.feedId
        currentView.findViewById<TextView>(R.id.feed_name).text = item.feedName

        return currentView
    }
}