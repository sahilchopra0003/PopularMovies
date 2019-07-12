package com.demo.popularmovies

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("movie/top_rated?api_key=aba67c472cb2c7822a699d9748c3f848")
    fun getMovies(): Observable<MoviesResponse>
}
