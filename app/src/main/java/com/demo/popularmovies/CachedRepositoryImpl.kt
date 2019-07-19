package com.demo.popularmovies

import io.reactivex.Observable

class CachedRepositoryImpl(val apiService: ApiService, val appDatabase: AppDatabase) : CachedRepository {

    private val movieDao = appDatabase.movieDao()

    override fun getMovies(): Observable<FetchEvent<List<Movie>>> {
        val inFlightEvents = movieDao
            .getAll()
            .map { FetchEvent(FetchAction.IN_FLIGHT, it, null) }
            .toObservable()

        val networkEvents = apiService
            .getMovies()
            .switchMap { response ->
                movieDao.insertMovies(response.results)
                return@switchMap Observable.just(response)
            }
            .switchMap { Observable.just(FetchEvent(FetchAction.SUCCESSFUL, it.results, null)) }
            .onErrorReturn { FetchEvent(FetchAction.FAILED, null, "Oops! Something went wrong.") }

        return Observable.concat(inFlightEvents, networkEvents)
    }
}
