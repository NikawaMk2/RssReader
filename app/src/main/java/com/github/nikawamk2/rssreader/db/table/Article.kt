package com.github.nikawamk2.rssreader.db.table

import java.lang.StringBuilder

class Article {
    companion object {
        var TableName = "ARTICLE"

        var Column_RssFeedId = "RSSFEED_ID"
        var Column_Id = "ID"
        var Column_Url = "URL"
        var Column_ArticleName = "ARTICLE_NAME"
        var Column_ArticleDate = "ARTICLE_DATE"

        fun getCreateTableSql(): String {
            val sql = StringBuilder()
            sql.append(" CREATE TABLE $TableName")
            sql.append(" (")
            sql.append("    $Column_RssFeedId TEXT NOT NULL,")
            sql.append("    $Column_Id TEXT NOT NULL,")
            sql.append("    $Column_Url TEXT NOT NULL,")
            sql.append("    $Column_ArticleName TEXT NOT NULL,")
            sql.append("    $Column_ArticleDate TEXT,")
            sql.append("    PRIMARY KEY($Column_Id),")
            sql.append("    FOREIGN KEY ($Column_RssFeedId)")
            sql.append("        REFERENCES ${RssFeed.TableName}(${RssFeed.Column_Id})")
            sql.append(" )")

            return sql.toString()
        }
    }
}