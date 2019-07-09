package com.demo.popularmovies

import io.reactivex.Observable

interface CachedRepository {
    fun getMovies(): Observable<FetchEvent<List<Movie>>>
}
