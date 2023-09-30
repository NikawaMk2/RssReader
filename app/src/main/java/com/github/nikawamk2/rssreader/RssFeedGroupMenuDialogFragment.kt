package com.github.nikawamk2.rssreader

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.DialogFragment

class RssFeedGroupMenuDialogFragment (private val groupList: RssFeedGroupListActivity, private val groupId: String, private val position: Int) : DialogFragment() {
    object PositionIndex {
        const val EditRssFeed = 0
        const val EditGroupName = 1
        const val Delete = 2
    }
    object ExtendData {
        const val GroupID = "Intent_GroupID"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setItems(R.array.group_menu,
                    DialogInterface.OnClickListener { _, which ->
                        when (which) {
                            PositionIndex.EditRssFeed -> transitionToRssFeedList()
                            PositionIndex.EditGroupName -> showEditGroupNameDialog()
                            PositionIndex.Delete -> showDeleteGroupDialog()
                        }
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    /**
     * RSSフィード一覧画面に遷移
     */
    private fun transitionToRssFeedList() {
        val intent = Intent(groupList, RssFeedListActivity::class.java)
        intent.putExtra(ExtendData.GroupID, groupId)
        startActivity(intent)
    }

    /**
     * グループ名編集ダイアログを表示
     */
    private fun showEditGroupNameDialog() {
        val groupName = AppCompatEditText(groupList)
        val builder = AlertDialog.Builder(groupList)
        val dialog = builder.setTitle(R.string.edit_group)
            .setMessage(R.string.input_new_group)
            .setView(groupName)
            .setPositiveButton(R.string.update) { dialog, _ ->
                dialog.cancel()
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                // 何もしない
            }
            .create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val errMsg = updateGroupName(groupName.text.toString())
            if (errMsg != "") {
                Toast.makeText(groupList, errMsg, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            dialog.cancel()
        }
    }

    /**
     * グループ削除を確認するダイアログを表示
     */
    private fun showDeleteGroupDialog() {
        AlertDialog.Builder(groupList)
            .setMessage(R.string.delete_group_confirm)
            .setPositiveButton(R.string.delete) { _, _ ->
                val errMsg = deleteGroup()
                if (errMsg != "") {
                    Toast.makeText(groupList, errMsg, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                // 何もしない
            }
            .show()
    }

    /**
     * グループ名を更新
     *
     * @args groupName グループ名
     * @return エラーメッセージ
     */
    private fun updateGroupName(groupName: String): String {
        if (groupName == "") {
            return groupList.resources.getString(R.string.group_name_empty)
        }

        try {
            val dm = DataManager(groupList)
            dm.updateRssFeedGroupName(groupId, groupName)
            groupList.updateGroupName(position, groupName)
        } catch (e: Exception) {
            return e.toString()
        }

        return ""
    }

    /**
     * グループを削除
     *
     * @return エラーメッセージ
     */
    private fun deleteGroup(): String {
        try {
            val dm = DataManager(groupList)
            dm.deleteRssFeedGroup(groupId)
            groupList.deleteGroupRow(position)
        } catch (e: Exception) {
            return e.toString()
        }

        return ""
    }
}