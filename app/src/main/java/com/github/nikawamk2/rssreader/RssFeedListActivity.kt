package com.github.nikawamk2.rssreader

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.FragmentManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.UUID

class RssFeedListActivity(private val groupId: String) : AppCompatActivity() {
    private lateinit var feedList: ArrayList<RssFeedInfo>
    private lateinit var adapter: RssFeedAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rss_feed_group_list)

        feedList = ArrayList()
        val list = findViewById<ListView>(R.id.rss_feed_group_list)

        adapter = RssFeedAdapter(
            this,
            feedList
        )
        list.adapter = adapter

        list.setOnItemClickListener { adapterView, view, position, id ->
            if (view == null) {
                return@setOnItemClickListener
            }

            val feedId = view.findViewById<TextView>(R.id.feed_id).text.toString()

            val dialog = RssFeedManuDialogFragment(this, feedId, position)
            val manager: FragmentManager = supportFragmentManager
            dialog.show(manager, "tag")
        }

        findViewById<FloatingActionButton>(R.id.rss_feed_fab).setOnClickListener { view ->
            val rssFeedUrl = AppCompatEditText(this)
            AlertDialog.Builder(this)
                .setTitle(R.string.add_rss)
                .setMessage(R.string.input_rss_feed_url)
                .setView(rssFeedUrl)
                .setPositiveButton(R.string.add) { _, _ ->
                    val errMsg = addRssFeed(rssFeedUrl.text.toString())
                    if (errMsg != "") {
                        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    // 何もしない
                }
                .show()
        }
    }

    fun deleteRssFeedRow(position: Int) {
        feedList.removeAt(position)

        for (i in 0..< feedList.count()) {
            val currentFeed = feedList[i]
            val newFeed = RssFeedInfo(i.toLong(), currentFeed.feedId, currentFeed.feedUrl, currentFeed.feedName)
            feedList[i] = newFeed
        }

        adapter.notifyDataSetChanged();
    }

    private fun addRssFeed(rssFeedUrl: String): String {
        if (rssFeedUrl == "") {
            return resources.getString(R.string.rss_feed_url_empty)
        }

        //TODO:RSSフィードURLのチェック処理作る
        try {
            val feedId = UUID.randomUUID().toString()
            val dm = DataManager(this)
            //TODO:RSSフィード名をサイト名にしたい
            val rssFeedName = "test"
            dm.addRssFeed(feedId, groupId, rssFeedUrl, rssFeedName)

            val newGroup = RssFeedInfo(feedList.count().toLong(), feedId, rssFeedUrl, rssFeedName)
            feedList.add(newGroup)
        } catch (e: Exception) {
            return e.toString()
        }

        return ""
    }
}