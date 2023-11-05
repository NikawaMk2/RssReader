package com.github.nikawamk2.rssreader.common

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import com.github.nikawamk2.rssreader.R
import java.lang.Exception

class Util {
    companion object {
        fun showErrorDialog(context: Context, exception: Exception) {
            val errorMsg = exception.toString()
            val builder = AlertDialog.Builder(context)
            val dialog = builder.setTitle(R.string.error)
                .setMessage(errorMsg)
                .setPositiveButton(R.string.copy_to_clip) { dialog, _ ->
                    dialog.cancel()
                }
                .setNegativeButton(R.string.close) { _, _ ->
                    // 何もしない
                }
                .create()
            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("errorMessage", errorMsg)
                clipboard.setPrimaryClip(clip)
            }
        }
    }
}