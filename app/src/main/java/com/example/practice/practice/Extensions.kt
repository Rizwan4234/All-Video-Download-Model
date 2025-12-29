package com.example.practice.practice

import android.content.Context
import android.widget.Toast
import com.example.practice.api.VideoFormat
import com.example.practice.api.VideoResponse
import com.example.practice.model.DownloadedVideo
import java.io.File

fun VideoResponse.getHighestResolutionFormat(): VideoFormat? {
    return formats.maxByOrNull { it.width * it.height }
}

fun Context.getInternalVideos(): List<DownloadedVideo> {
    val dir = File(filesDir, "Videos")
    if (!dir.exists()) return emptyList()

    return dir.listFiles { file ->
        file.isFile && file.extension.equals("mp4", ignoreCase = true)
    }?.map { file ->
        DownloadedVideo(
            name = file.name,
            path = file.absolutePath
        )
    } ?: emptyList()
}
fun Context.getClipboardUrl(): String? {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
    val text = clipboard?.primaryClip?.getItemAt(0)?.coerceToText(this)?.toString()?.trim()
    if (text.isNullOrEmpty()) return null
    return if (android.util.Patterns.WEB_URL.matcher(text).matches()) text else null
}


fun Context.showToastMessage(message: String){
    Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
}