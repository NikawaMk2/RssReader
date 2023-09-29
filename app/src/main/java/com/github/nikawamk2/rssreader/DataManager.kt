package com.github.nikawamk2.rssreader

import android.content.ContentValues
import android.content.Context
import android.text.TextUtils
import com.github.nikawamk2.rssreader.db.RssReaderDBHelper
import com.github.nikawamk2.rssreader.db.table.Article
import com.github.nikawamk2.rssreader.db.table.RssFeed
import com.github.nikawamk2.rssreader.db.table.RssFeedGroup
import com.github.nikawamk2.rssreader.models.ArticleInfo
import com.github.nikawamk2.rssreader.models.RssFeedGroupInfo
import com.github.nikawamk2.rssreader.models.RssFeedInfo
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.text.StringBuilder

class DataManager(context: Context) {
    private val dbHelper: RssReaderDBHelper = RssReaderDBHelper(context)

    /**
     * RSSフィードグループ名を更新
     *
     * @args groupId グループID
     * @args groupName グループ名
     */
    fun updateRssFeedGroupName(groupId: String, groupName: String) {
        val db = dbHelper.writableDatabase

        val values = ContentValues()
        values.put(RssFeedGroup.Column_GroupName, groupName)

        val whereClauses = "${RssFeedGroup.Column_Id} = ?"
        val whereArgs = arrayOf(groupId)

        db.update(RssFeedGroup.TableName, values, whereClauses, whereArgs)
    }

    /**
     * RSSフィードグループを追加
     *
     * @args groupId グループID
     * @args groupName グループ名
     */
    fun addRssFeedGroup(groupId: String, groupName: String) {
        val db = dbHelper.writableDatabase

        val values = ContentValues()
        values.put(RssFeedGroup.Column_Id, groupId)
        values.put(RssFeedGroup.Column_GroupName, groupName)

        db.insert(RssFeedGroup.TableName, null, values)
    }

    /**
     * RSSフィードグループを削除
     *
     * @args groupId グループID
     */
    fun deleteRssFeedGroup(groupId: String) {
        val db = dbHelper.writableDatabase

        val selection = "${RssFeedGroup.Column_Id} = ?"
        val selectionArgs = arrayOf(groupId)

        db.delete(RssFeedGroup.TableName, selection, selectionArgs)
    }

    /**
     * RSSフィードグループ一覧を取得
     *
     * @return RSSフィードグループ一覧
     */
    fun getRssFeedGroup(): ArrayList<RssFeedGroupInfo> {
        val db = dbHelper.readableDatabase

        val groupList = ArrayList<RssFeedGroupInfo>()

        val projection = arrayOf(RssFeedGroup.Column_Id, RssFeedGroup.Column_GroupName)

        val cursor = db.query(
            RssFeedGroup.TableName,
            projection,
            null,
            null,
            null,
            null,
            null
        )
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

    /**
     * RSSフィードを追加
     *
     * @args feedId フィードID
     * @args groupId グループID
     * @args rssFeedUrl RSSフィードURL
     * @args rssFeedName RSSフィード名
     */
    fun addRssFeed(feedId: String, groupId: String, rssFeedUrl: String, rssFeedName: String) {
        val db = dbHelper.writableDatabase

        val values = ContentValues()
        values.put(RssFeed.Column_GroupId, groupId)
        values.put(RssFeed.Column_Id, feedId)
        values.put(RssFeed.Column_Url, rssFeedUrl)
        values.put(RssFeed.Column_FeedName, rssFeedName)

        db.insert(RssFeed.TableName, null, values)
    }

    /**
     * RSSフィードを削除
     *
     * @args feedId RSSフィードID
     */
    fun deleteRssFeed(feedId: String) {
        val db = dbHelper.writableDatabase

        val selection = "${RssFeed.Column_Id} = ?"
        val selectionArgs = arrayOf(feedId)

        db.delete(RssFeed.TableName, selection, selectionArgs)
    }

    /**
     * RSSフィードを取得
     *
     * @args groupId グループID
     * @return RSSフィード一覧
     */
    fun getRssFeed(groupId: String): ArrayList<RssFeedInfo> {
        val db = dbHelper.readableDatabase

        val feedList = ArrayList<RssFeedInfo>()

        val projection = arrayOf(RssFeed.Column_Id, RssFeed.Column_Url, RssFeed.Column_FeedName)

        val selection = "${RssFeed.Column_GroupId} = ?"
        val selectionArgs = arrayOf(groupId)

        val cursor = db.query(
            RssFeed.TableName,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        if (cursor.count == 0) {
            return feedList;
        }

        var listIndex: Long = 0
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val groupInfo = RssFeedInfo(
                listIndex,
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2)
            )
            feedList.add(groupInfo)
            cursor.moveToNext()
            listIndex++
        }
        return feedList;
    }

    /**
     * グループIDに紐づく記事を洗い変えする
     *
     * @args groupId グループID
     * @args articleList 登録対象の記事一覧
     */
    fun refreshArticle(groupId: String, articleList: ArrayList<ArticleInfo>) {
        if (articleList.isEmpty()) {
            return
        }

        val db = dbHelper.writableDatabase

        val currentArticle = getArticle(groupId)
        if (currentArticle.isNotEmpty()) {
            val bindList = ArrayList<String>()
            for (i in 0 ..< currentArticle.count()) {
                bindList.add("?")
            }
            val selection = "${Article.Column_Id} IN (${TextUtils.join(",", bindList)})"
            val selectionArgs = currentArticle.map{ it.articleId }.toTypedArray()
            db.delete(Article.TableName, selection, selectionArgs)
        }

        for (article in articleList) {
            val values = ContentValues()
            values.put(Article.Column_RssFeedId, article.rssFeedId)
            values.put(Article.Column_Id, article.articleId)
            values.put(Article.Column_Url, article.articleUrl)
            values.put(Article.Column_ArticleName, article.articleName)
            values.put(Article.Column_ArticleDate, article.getArticleDateForDb())

            db.insert(Article.TableName, null, values)
        }
    }

    /**
     * 記事一覧を取得
     *
     * @args groupId グループID
     * @return 記事一覧
     */
    fun getArticle(groupId: String): ArrayList<ArticleInfo> {
        val db = dbHelper.readableDatabase

        val articleList = ArrayList<ArticleInfo>()

        val cursor = db.rawQuery(getArticleSql(groupId), null, null)
        if (cursor.count == 0) {
            return articleList;
        }

        val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        var listIndex: Long = 0
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val articleDateStr = cursor.getString(5)
            var articleDate = LocalDateTime.MIN
            if (articleDateStr != "") {
                articleDate = LocalDateTime.parse(cursor.getString(5), format)
            }
            val articleInfo = ArticleInfo(
                listIndex,
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                articleDate
            )
            articleList.add(articleInfo)
            cursor.moveToNext()
            listIndex++
        }
        return articleList;
    }

    /**
     * 記事を取得するSQL
     *
     * @args groupId グループID
     */
    private fun getArticleSql(groupId: String): String {
        val sql = StringBuilder()
        sql.append(" SELECT")
        sql.append("     AR.${Article.Column_RssFeedId},")
        sql.append("     AR.${Article.Column_Id},")
        sql.append("     AR.${Article.Column_Url},")
        sql.append("     AR.${Article.Column_ArticleName},")
        sql.append("     RF.${RssFeed.Column_FeedName},")
        sql.append("     AR.${Article.Column_ArticleDate}")
        sql.append(" FROM")
        sql.append("     ${Article.TableName} AR")
        sql.append(" INNER JOIN ${RssFeed.TableName} RF")
        sql.append("     ON AR.${Article.Column_RssFeedId} = RF.${RssFeed.Column_Id}")
        sql.append(" INNER JOIN ${RssFeedGroup.TableName} RG")
        sql.append("     ON RF.${RssFeed.Column_GroupId} = RG.${RssFeedGroup.Column_Id}")
        sql.append(" WHERE")
        sql.append("     RG.${RssFeedGroup.Column_Id} = '$groupId'")

        return sql.toString()
    }
}