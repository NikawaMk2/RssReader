package com.github.nikawamk2.rssreader.rss

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.mcsoxford.rss.RSSFeed
import org.mcsoxford.rss.RSSReader


class Rss(private val rssFeedUrl: String) {
    private val rssReader: RSSReader = RSSReader()

    fun getRssFeed():RSSFeed?  = runBlocking {
        var rssFeed: RSSFeed? = null
        val job = GlobalScope.launch {
            rssFeed = rssReader.load(rssFeedUrl)
        }
        job.join()

        return@runBlocking rssFeed
    }
}