package com.github.nikawamk2.rssreader.db.table

import java.lang.StringBuilder

class RssFeedGroup {
    companion object {
        var TableName = "RSSFEEDGROUP"

        var Column_Id = "ID"
        var Column_GroupName = "GROUP_NAME"

        fun getCreateTableSql(): String {
            val sql = StringBuilder()
            sql.append(" CREATE TABLE $TableName")
            sql.append(" (")
            sql.append("    $Column_Id TEXT NOT NULL PRIMARY KEY,")
            sql.append("    $Column_GroupName TEXT NOT NULL")
            sql.append(" )")

            return sql.toString()
        }
    }
}