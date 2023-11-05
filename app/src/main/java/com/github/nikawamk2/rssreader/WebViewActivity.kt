package com.github.nikawamk2.rssreader

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity


class WebViewActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        setSupportActionBar(findViewById(R.id.web_view_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val articleUrl = intent.getStringExtra(MainActivity.ExtendData.ArticleUrl).toString()
        val articleName = intent.getStringExtra(MainActivity.ExtendData.ArticleName).toString()
        title = articleName

        val webView = findViewById<WebView>(R.id.web_view)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return false
            }
        }
        webView.loadUrl(articleUrl)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            androidx.appcompat.R.anim.abc_fade_out,
            androidx.appcompat.R.anim.abc_fade_out
        )

    }
}