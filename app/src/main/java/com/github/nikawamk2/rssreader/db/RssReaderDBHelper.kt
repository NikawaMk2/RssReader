package com.github.nikawamk2.rssreader.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.github.nikawamk2.rssreader.db.table.RssFeed
import com.github.nikawamk2.rssreader.db.table.RssFeedGroup

class RssReaderDBHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(RssFeedGroup.getCreateTableSql())
        db.execSQL(RssFeed.getCreateTableSql())
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "RssReader.db"
    }
}