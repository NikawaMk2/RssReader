package com.github.nikawamk2.rssreader

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.FragmentManager
import com.github.nikawamk2.rssreader.models.RssFeedInfo
import com.github.nikawamk2.rssreader.rss.Rss
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.mcsoxford.rss.RSSReaderException
import java.util.UUID


class RssFeedListActivity : AppCompatActivity() {
    private lateinit var feedList: ArrayList<RssFeedInfo>
    private lateinit var adapter: RssFeedAdapter
    private lateinit var groupId: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rss_feed_list)

        setSupportActionBar(findViewById(R.id.rss_feed_list_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        groupId = intent.getStringExtra(RssFeedGroupMenuDialogFragment.ExtendData.GroupID).toString()

        val dm = DataManager(this)
        feedList = dm.getRssFeed(groupId)
        val list = findViewById<ListView>(R.id.rss_feed_list)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * RSSフィードをの行を削除
     *
     * @args position 削除対象の行番号
     */
    fun deleteRssFeedRow(position: Int) {
        feedList.removeAt(position)

        for (i in 0..< feedList.count()) {
            val currentFeed = feedList[i]
            val newFeed = RssFeedInfo(i.toLong(), currentFeed.feedId, currentFeed.feedUrl, currentFeed.feedName)
            feedList[i] = newFeed
        }

        adapter.notifyDataSetChanged()
    }

    /**
     * RSSフィードを追加
     *
     * @args rssFeedUrl 追加するRSSフィードURL
     * @return エラーメッセージ
     */
    private fun addRssFeed(rssFeedUrl: String): String {
        if (rssFeedUrl == "") {
            return resources.getString(R.string.rss_feed_url_empty)
        }

        try {
            val rss = Rss(rssFeedUrl)
            val feed = rss.getRssFeed() ?: throw RSSReaderException(0, "")

            val feedId = UUID.randomUUID().toString()
            val dm = DataManager(this)
            val rssFeedName = feed.title
            dm.addRssFeed(feedId, groupId, rssFeedUrl, rssFeedName)

            val newGroup = RssFeedInfo(feedList.count().toLong(), feedId, rssFeedUrl, rssFeedName)
            feedList.add(newGroup)
        } catch (e: RSSReaderException) {
            return resources.getString(R.string.rss_feed_url_not_correct)
        } catch (e: Exception) {
            return e.toString()
        }

        return ""
    }
}