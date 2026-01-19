package com.example.practice.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue

object RetrofitInstance {


    private val client = OkHttpClient.Builder().build()

    val api: VideoApi = Retrofit.Builder()
      //  .baseUrl("http://159.203.143.191/")
        .baseUrl("https://redhole.cofencode.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(VideoApi::class.java)
}