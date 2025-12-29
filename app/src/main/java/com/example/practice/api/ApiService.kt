package com.example.practice.api

import com.example.practice.model.DownloadRequest
import com.example.practice.model.DownloadResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface VideoApi{

    @FormUrlEncoded
    @POST("info/universal")
    suspend fun fetchVideoInfo(
        @Field("url") videoUrl: String
    ): VideoResponse
}