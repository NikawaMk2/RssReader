package com.github.nikawamk2.rssreader

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceDataStore
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SettingsFragment()).commit()
    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
        if (pref.key == "edit_rss_feed") {
            transitionCategoryList()
        }

        return true
    }

    private fun transitionCategoryList() {
        val intent = Intent(this, RssFeedGroupListActivity::class.java)
        startActivity(intent)
    }
}