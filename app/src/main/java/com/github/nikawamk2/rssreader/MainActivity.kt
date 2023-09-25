package com.github.nikawamk2.rssreader

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import com.github.nikawamk2.rssreader.databinding.ActivityMainBinding
import com.github.nikawamk2.rssreader.db.RssReaderDBHelper
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tabLayout = findViewById(R.id.rssGroupListTab)

        setSupportActionBar(binding.toolbar)

        // TabLayoutの生成
        tabLayout.also {
            val dm = DataManager(this)
            val groupList = dm.getRssFeedGroup()
            for (group in groupList) {
                val tab = it.newTab()
                tab.text = group.groupName
                it.addTab(tab)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        tabLayout.also {
            val dm = DataManager(this)
            val groupList = dm.getRssFeedGroup()
            for(group in groupList) {
                val tab = it.newTab()
                tab.text = group.groupName
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

    fun addTab(tabName: String) {
        val tab = tabLayout.newTab()
        tab.text = tabName
        tabLayout.addTab(tab)
    }

    private fun transitionToSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}