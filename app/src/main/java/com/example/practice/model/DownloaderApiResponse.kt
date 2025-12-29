package com.example.practice.model

import androidx.annotation.Keep

@Keep
data class VideoResponse(
    val formats: List<Format>,
    val like_count: Long?,
    val platform: String?,
    val thumbnail: String?,
    val title: String?,
    val uploader: String?,
    val view_count: Long?
)
@Keep
data class Format(
    val abr: Double?,
    val acodec: String?,
    val aspect_ratio: Double?,
    val asr: Int?,
    val audio_ext: String?,
    val container: String?,
    val downloader_options: DownloaderOptions?,
    val dynamic_range: String?,
    val ext: String?,
    val filesize: Long?,
    val filesize_approx: Long?,
    val format: String?,
    val format_id: String?,
    val format_note: String?,
    val fps: Double?,
    val height: Int?,
    val http_headers: HttpHeaders?,
    val is_dash_periods: Boolean?,
    val language: String?,
    val manifest_stream_number: Int?,
    val manifest_url: String?,
    val protocol: String?,
    val quality: Int?,
    val resolution: String?,
    val tbr: Double?,
    val url: String?,
    val vbr: Double?,
    val vcodec: String?,
    val video_ext: String?,
    val width: Int?
)

@Keep
data class DownloaderOptions(
    val http_chunk_size: Long?
)

@Keep
data class HttpHeaders(
    val Accept: String?,
    val `Accept-Language`: String?,
    val `Sec-Fetch-Mode`: String?,
    val `User-Agent`: String?
)
