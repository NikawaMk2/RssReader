package com.github.nikawamk2.rssreader

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.github.nikawamk2.rssreader.models.RssFeedGroupInfo

class RssFeedGroupAdapter(private val context: Context, private val rssFeedGroupList: ArrayList<RssFeedGroupInfo>) : BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    override fun getCount(): Int {
        return rssFeedGroupList.size
    }

    override fun getItem(position: Int): Any {
        return rssFeedGroupList[position]
    }

    override fun getItemId(position: Int): Long {
        return rssFeedGroupList[position].itemId
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = rssFeedGroupList[position];

        var currentView = convertView
        if(convertView == null){
            currentView = inflater.inflate(R.layout.row_rss_feed_group, null);
        }

        if (currentView == null) {
            return View(context);
        }

        currentView.findViewById<TextView>(R.id.group_id).text = item.groupId
        currentView.findViewById<TextView>(R.id.group_name).text = item.groupName

        return currentView
    }
}