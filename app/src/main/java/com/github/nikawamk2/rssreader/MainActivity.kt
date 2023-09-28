package com.github.nikawamk2.rssreader

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.nikawamk2.rssreader.databinding.ActivityMainBinding
import com.github.nikawamk2.rssreader.rss.Rss
import com.google.android.material.tabs.TabLayout
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var articleList: ArrayList<ArticleInfo>
    private lateinit var adapter: ArticleAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var tabLayout: TabLayout

    object ExtendData {
        const val ArticleUrl = "Intent_ArticleUrl"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tabLayout = findViewById(R.id.rssGroupListTab)

        setSupportActionBar(binding.toolbar)

        tabLayout.also {
            val dm = DataManager(this)
            val groupList = dm.getRssFeedGroup()
            for (group in groupList) {
                val tab = it.newTab()
                tab.tag = group.groupId
                tab.text = group.groupName
                it.addTab(tab)
            }
        }
        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val newArticleList = getCurrentArticle()
                articleList.clear()
                articleList.addAll(newArticleList)
                adapter.notifyDataSetChanged()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // 処理をしない
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 処理をしない
            }
        })

        val list = findViewById<ListView>(R.id.article_list)
        articleList = getCurrentArticle()
        adapter = ArticleAdapter(
            this,
            articleList
        )
        list.adapter = adapter
        list.setOnItemClickListener { adapterView, view, position, id ->
            if (view == null) {
                return@setOnItemClickListener
            }

            val articleUrl = view.findViewById<TextView>(R.id.article_url).text.toString()

            trasitionToWebView(articleUrl)
        }

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.article_refresh)
        swipeRefreshLayout.setOnRefreshListener {
            refreshCurrentArticle()
            swipeRefreshLayout.isRefreshing = false
        }
        swipeRefreshLayout.viewTreeObserver.addOnScrollChangedListener {
            list.isEnabled = list.scrollY == 0;
        }
    }

    override fun onResume() {
        super.onResume()
        val dm = DataManager(this)
        val groupList = dm.getRssFeedGroup()

        val tabList = ArrayList<TabLayout.Tab>()
        for (i in 0..< tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i) ?: continue

            // タブ削除
            if (!groupList.any{ it.groupId == tab.tag }) {
                tabLayout.removeTabAt(i)
            }

            tabList.add(tab)
        }

        // タブ追加
        val newGroupList = groupList.filter { group ->
            !tabList.any{ it.tag == group.groupId }
        }
        tabLayout.also {
            for (newGroup in newGroupList) {
                val tab = it.newTab()
                tab.tag = newGroup.groupId
                tab.text = newGroup.groupName
                it.addTab(tab)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> transitionToSettings()
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }

    private fun getCurrentArticle(): ArrayList<ArticleInfo> {
        val tab = tabLayout.getTabAt(tabLayout.selectedTabPosition) ?: return ArrayList<ArticleInfo>()
        val groupId = tab.tag.toString()

        val dm = DataManager(this)
        return dm.getArticle(groupId)
    }

    private fun refreshCurrentArticle() {
        val tab = tabLayout.getTabAt(tabLayout.selectedTabPosition) ?: return
        val groupId = tab.tag.toString()

        val dm = DataManager(this)
        val feedList = dm.getRssFeed(groupId)

        val newArticleList = ArrayList<ArticleInfo>()
        for (i in 0..<feedList.count()) {
            val feed = feedList[i]
            val rss = Rss(feed.feedUrl)
            val feedData = rss.getRssFeed() ?: throw Exception()
            feedData.items.forEachIndexed { index, element ->
                val article = ArticleInfo(
                    index.toLong(),
                    feed.feedId,
                    UUID.randomUUID().toString(),
                    element.link.toString(),
                    element.title
                )
                newArticleList.add(article)
            }
        }

        articleList.clear()
        articleList.addAll(newArticleList)
        adapter.notifyDataSetChanged()

        dm.refreshArticle(groupId, newArticleList)
    }

    private fun transitionToSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun trasitionToWebView(articleUrl: String) {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra(ExtendData.ArticleUrl, articleUrl)
        startActivity(intent)
        overridePendingTransition(
            androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom,
            androidx.appcompat.R.anim.abc_fade_in
        )
    }
}