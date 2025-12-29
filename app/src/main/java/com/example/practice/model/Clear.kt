package com.example.practice.model

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.net.URLDecoder

fun cleanVideoUrl(rawUrl: String): String {
    return try {
        if (rawUrl.contains("google.com/url")) {
            val uri = Uri.parse(rawUrl)
            val encoded = uri.getQueryParameter("q") ?: rawUrl
            URLDecoder.decode(encoded, "UTF-8")
        } else {
            URLDecoder.decode(rawUrl, "UTF-8")
        }
    } catch (e: Exception) {
        rawUrl
    }
}


