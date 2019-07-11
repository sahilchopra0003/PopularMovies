package com.demo.popularmovies

import io.reactivex.Observable

interface ApiService {

    fun getMovies(): Observable<MoviesResponse>

}