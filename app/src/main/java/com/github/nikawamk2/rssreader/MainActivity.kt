package com.github.nikawamk2.rssreader

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.nikawamk2.rssreader.databinding.ActivityMainBinding
import com.github.nikawamk2.rssreader.db.RssReaderDBHelper
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tabLayout: TabLayout

    @SuppressLint("ResourceType")
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

    private fun transitionToSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}