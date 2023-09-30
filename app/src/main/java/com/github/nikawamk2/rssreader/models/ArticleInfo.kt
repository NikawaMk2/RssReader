package com.github.nikawamk2.rssreader.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ArticleInfo(
    val itemId: Long,
    val rssFeedId: String,
    val articleId: String,
    val articleUrl: String,
    val articleName: String,
    val feedName: String,
    val articleDate: LocalDateTime
) {
    /**
     * 記事作成日時をDB格納用の形式で取得
     *
     * @return 記事作成日時(yyyy-MM-dd HH:mm:ss)LocalDateTime.MINの場合は空文字で返す
     */
    fun getArticleDateForDb(): String {
        if (articleDate == LocalDateTime.MIN) {
            return ""
        }

        val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return articleDate.format(dtf)
    }
}