package com.github.nikawamk2.rssreader.db.table

import java.lang.StringBuilder

class RssFeed {
    companion object {
        var TableName = "RSSFEED"

        var Column_GroupId = "GROUP_ID"
        var Column_Id = "ID"
        var Column_Url = "URL"
        var Column_FeedName = "FEED_NAME"

        fun getCreateTableSql(): String {
            var sql = StringBuilder()
            sql.append(" CREATE TABLE $TableName")
            sql.append(" (")
            sql.append("    $Column_GroupId TEXT NOT NULL,")
            sql.append("    $Column_Id TEXT NOT NULL,")
            sql.append("    $Column_Url NOT NULL,")
            sql.append("    $Column_FeedName NOT NULL,")
            sql.append("    PRIMARY KEY($Column_GroupId, $Column_Id),")
            sql.append("    FOREIGN KEY ($Column_GroupId)")
            sql.append("        REFERENCES ${RssFeedGroup.TableName}(${RssFeedGroup.Column_Id})")
            sql.append(" )")

            return sql.toString()
        }
    }
}