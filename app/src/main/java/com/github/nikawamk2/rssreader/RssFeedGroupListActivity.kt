package com.github.nikawamk2.rssreader

import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.FragmentManager
import com.github.nikawamk2.rssreader.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.UUID

class RssFeedGroupListActivity : AppCompatActivity()  {

    private lateinit var groupList: ArrayList<RssFeedGroupInfo>
    private lateinit var adapter: RssFeedGroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rss_feed_group_list)

        groupList = ArrayList()
        val list = findViewById<ListView>(R.id.rss_feed_group_list)

        adapter = RssFeedGroupAdapter(
            this,
            groupList
        )
        list.adapter = adapter

        list.setOnItemClickListener { adapterView, view, position, id ->
            if (view == null) {
                return@setOnItemClickListener
            }

            val groupId = view.findViewById<TextView>(R.id.group_id).text.toString()

            val dialog = RssFeedGroupMenuDialogFragment(this, groupId, position)
            val manager: FragmentManager = supportFragmentManager
            dialog.show(manager, "tag")
        }

        findViewById<FloatingActionButton>(R.id.rss_feed_group_fab).setOnClickListener { view ->
            val groupName = AppCompatEditText(this)
            AlertDialog.Builder(this)
                .setTitle(R.string.add_group)
                .setMessage(R.string.input_group)
                .setView(groupName)
                .setPositiveButton(R.string.add) { _, _ ->
                    val errMsg = addGroup(groupName.text.toString())
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

    fun updateGroupName(position: Int, newGroupName: String) {
        val oldGroup = groupList[position]
        val newGroup = RssFeedGroupInfo(oldGroup.itemId, oldGroup.groupId, newGroupName)
        groupList[position] = newGroup
    }

    fun deleteGroupRow(position: Int) {
        groupList.removeAt(position)

        for (i in 0..< groupList.count()) {
            val currentGroup = groupList[i]
            val newGroup = RssFeedGroupInfo(i.toLong(), currentGroup.groupId, currentGroup.groupName)
            groupList[i] = newGroup
        }

        adapter.notifyDataSetChanged();
    }

    private fun addGroup(groupName: String): String {
        if (groupName == "") {
            return resources.getString(R.string.group_name_empty)
        }

        try {
            val groupId = UUID.randomUUID().toString()
            val dm = DataManager(this)
            dm.addRssFeedGroup(groupId, groupName)

            val newGroup = RssFeedGroupInfo(groupList.count().toLong(), groupId, groupName)
            groupList.add(newGroup)
        } catch (e: Exception) {
            return e.toString()
        }

        return ""
    }
}