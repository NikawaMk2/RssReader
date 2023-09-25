package com.github.nikawamk2.rssreader

import android.content.ContentValues
import android.content.Context
import com.github.nikawamk2.rssreader.db.RssReaderDBHelper
import com.github.nikawamk2.rssreader.db.table.RssFeed
import com.github.nikawamk2.rssreader.db.table.RssFeedGroup
import java.lang.StringBuilder
import java.util.UUID

class DataManager(context: Context) {
    private val dbHelper: RssReaderDBHelper = RssReaderDBHelper(context)

    fun updateRssFeedGroupName(groupId: String, groupName: String) {
        val db = dbHelper.writableDatabase

        val values = ContentValues()
        values.put(RssFeedGroup.Column_GroupName, groupName)

        val whereClauses = "${RssFeedGroup.Column_Id} = ?"
        val whereArgs = arrayOf(groupId)

        db.update(RssFeedGroup.TableName, values, whereClauses, whereArgs)
    }

    fun addRssFeedGroup(groupId: String, groupName: String) {
        val db = dbHelper.writableDatabase

        val values = ContentValues()
        values.put(RssFeedGroup.Column_Id, groupId)
        values.put(RssFeedGroup.Column_GroupName, groupName)

        db.insert(RssFeedGroup.TableName, null, values)
    }

    fun deleteRssFeedGroup(groupId: String) {
        val db = dbHelper.writableDatabase

        val selection = "${RssFeedGroup.Column_Id} = ?"
        val selectionArgs = arrayOf(groupId)

        db.delete(RssFeedGroup.TableName, selection, selectionArgs)
    }

    fun getRssFeedGroup(): ArrayList<RssFeedGroupInfo> {
        val db = dbHelper.readableDatabase

        val groupList = ArrayList<RssFeedGroupInfo>()
        val cursor = db.rawQuery(getRssFeedGroupSql(), null)
        if (cursor.count == 0) {
            return groupList;
        }

        var listIndex: Long = 0
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val groupInfo = RssFeedGroupInfo(listIndex, cursor.getString(0), cursor.getString(1))
            groupList.add(groupInfo)
            cursor.moveToNext()
            listIndex++
        }
        return groupList;
    }

    fun getRssFeedGroupId(): ArrayList<String> {
        val db = dbHelper.readableDatabase

        val groupList = ArrayList<String>()
        val cursor = db.rawQuery(getRssFeedGroupSql(), null)
        if (cursor.count == 0) {
            return groupList;
        }

        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            groupList.add(cursor.getString(0))
            cursor.moveToNext()
        }
        return groupList;
    }

    fun addRssFeed(feedId: String, groupId: String, rssFeedUrl: String, rssFeedName: String) {
        val db = dbHelper.writableDatabase

        val values = ContentValues()
        values.put(RssFeed.Column_GroupId, groupId)
        values.put(RssFeed.Column_Id, feedId)
        values.put(RssFeed.Column_Url, rssFeedUrl)
        values.put(RssFeed.Column_FeedName, rssFeedName)

        db.insert(RssFeed.TableName, null, values)
    }

    fun deleteRssFeed(feedId: String) {
        val db = dbHelper.writableDatabase

        val selection = "${RssFeed.Column_Id} = ?"
        val selectionArgs = arrayOf(feedId)

        db.delete(RssFeed.TableName, selection, selectionArgs)
    }

    private fun getRssFeedGroupSql(): String {
        val sql = StringBuilder()
        sql.append(" SELECT")
        sql.append("    ${RssFeedGroup.Column_Id},")
        sql.append("    ${RssFeedGroup.Column_GroupName}")
        sql.append(" FROM ${RssFeedGroup.TableName}")

        return sql.toString()
    }
}