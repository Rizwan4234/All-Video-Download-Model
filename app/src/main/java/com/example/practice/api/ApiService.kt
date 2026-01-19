package com.example.practice.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface VideoApi{

    @FormUrlEncoded
    @POST("info/universal")
    suspend fun fetchVideoInfo(
        @Field("url") videoUrl: String
    ): VideoResponse
}