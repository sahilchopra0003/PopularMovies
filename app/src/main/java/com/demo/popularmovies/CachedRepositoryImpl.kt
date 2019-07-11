package com.demo.popularmovies

import io.reactivex.Observable

class CachedRepositoryImpl(val apiService: ApiService) : CachedRepository {

    override fun getMovies(): Observable<FetchEvent<List<Movie>>> {
        val inFlightEvents = Observable
            .just(FetchEvent<List<Movie>>(FetchAction.IN_FLIGHT, null, null))

        val networkEvents = apiService
            .getMovies()
            .switchMap { Observable.just(FetchEvent(FetchAction.SUCCESSFUL, it.results, null)) }
            .onErrorReturn { FetchEvent(FetchAction.FAILED,null, "Oops! Something went wrong.") }

        return Observable.concat(inFlightEvents, networkEvents)
    }
}
