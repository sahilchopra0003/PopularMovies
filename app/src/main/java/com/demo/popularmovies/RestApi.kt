package com.demo.popularmovies

import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

const val BASE_URL = "https://api.themoviedb.org/3/"

object RestApi {

    private val builder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

    private val httpClient = OkHttpClient.Builder().build()

    private val retrofit = builder
        .client(httpClient)
        .build()

    private val service = retrofit.create(ApiService::class.java)

    fun getService(): ApiService {
        return service
    }
}
