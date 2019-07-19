package com.demo.popularmovies

import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CachedRepositoryImpl(val apiService: ApiService, val appDatabase: AppDatabase) : CachedRepository {

    private val movieDao = appDatabase.movieDao()

    override fun getMovies(): Observable<FetchEvent<List<Movie>>> {
        val inFlightEvents = movieDao
            .getAll()
            .map { FetchEvent(FetchAction.IN_FLIGHT, it, null) }
            .toObservable()

        val networkEvents = apiService
            .getMovies()
            .doOnNext { Log.i("123Before SwitchMap", it.toString()) }
            .switchMap { response ->
                movieDao.insertMovies(response.results)
                return@switchMap Observable.just(response)
            }
            .doOnNext { Log.i("123After SwitchMap", it.toString()) }
            .switchMap { Observable.just(FetchEvent(FetchAction.SUCCESSFUL, it.results, null)) }
            .onErrorReturn {
                Log.i("Error SwitchMap", it.toString())
                FetchEvent(FetchAction.FAILED, null, "Oops! Something went wrong.")
            }

        return Observable
            .concat(inFlightEvents, networkEvents)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
