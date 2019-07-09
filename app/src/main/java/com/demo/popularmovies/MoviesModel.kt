package com.demo.popularmovies

import io.reactivex.Observable

object MoviesModel {
    fun bind(
        lifecycle: Observable<MviLifecycle>,
        repository: CachedRepository
    ): Observable<MoviesState> {
        return lifecycle
            .filter { it == MviLifecycle.CREATED }
            .switchMap {
                repository
                    .getMovies()
                    .map { MoviesState(it.result ?: emptyList(), it.fetchAction, it.error) }
            }
    }
}