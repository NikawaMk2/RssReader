package com.github.nikawamk2.rssreader

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class RssFeedManuDialogFragment (private val rssFeedList: RssFeedListActivity, private val feedId: String, private val position: Int) : DialogFragment() {
    object PositionIndex {
        const val Delete = 0
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setItems(R.array.group_menu,
                DialogInterface.OnClickListener { _, which ->
                    when (which) {
                        PositionIndex.Delete -> showDeleteRssFeedDialog()
                    }
                })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    /**
     * RSSフィード一覧画面に遷移
     */
    private fun showDeleteRssFeedDialog() {
        AlertDialog.Builder(rssFeedList)
            .setMessage(R.string.delete_rss_feed_confirm)
            .setPositiveButton(R.string.delete) { _, _ ->
                val errMsg = deleteRssFeed()
                if (errMsg != "") {
                    Toast.makeText(rssFeedList, errMsg, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                // 何もしない
            }
            .show()
    }

    /**
     * RSSフィードを削除
     *
     */
    private fun deleteRssFeed(): String {
        try {
            val dm = DataManager(rssFeedList)
            dm.deleteRssFeed(feedId)
            rssFeedList.deleteRssFeedRow(position)
        } catch (e: Exception) {
            return e.toString()
        }

        return ""
    }
}