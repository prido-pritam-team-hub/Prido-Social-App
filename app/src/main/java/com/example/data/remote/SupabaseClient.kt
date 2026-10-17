package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object SupabaseClient {
    const val BASE_URL = "https://pbgvysktxchcktnaplti.supabase.co/"
    const val PUBLISHABLE_KEY = "sb_publishable_SP3R_hMPuq49gVEARKqvVQ_h60nLm1x"

    private val headerInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()
            .header("apikey", PUBLISHABLE_KEY)
            .header("Accept", "application/json")

        // If authorization is not explicitly provided on request, default to the anon/publishable key
        if (original.header("Authorization") == null) {
            builder.header("Authorization", "Bearer $PUBLISHABLE_KEY")
        }

        chain.proceed(builder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: SupabaseApi = retrofit.create(SupabaseApi::class.java)
}
