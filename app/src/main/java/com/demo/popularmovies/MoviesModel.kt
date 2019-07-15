package com.demo.popularmovies

import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom

object MoviesModel {
  fun bind(
      lifecycle: Observable<MviLifecycle>,
      userIntentions: MoviesIntention,
      repository: CachedRepository,
      previousStates: Observable<MoviesState>
  ): Observable<MoviesState> {
    return Observable.merge(
        lifecycleCreatedUsecase(lifecycle, repository),
        lifecycleResumedUsecase(lifecycle, previousStates),
        refreshListUseCase(userIntentions, repository)
    )
  }

  fun lifecycleCreatedUsecase(
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

  fun lifecycleResumedUsecase(
      lifecycle: Observable<MviLifecycle>,
      previousStates: Observable<MoviesState>
  ): Observable<MoviesState> {
    return lifecycle
        .filter { it == MviLifecycle.RESUMED }
        .withLatestFrom(previousStates)
        .map { (_, previousState) -> previousState }
  }

  fun refreshListUseCase(
      userIntentions: MoviesIntention,
      repository: CachedRepository
  ): Observable<MoviesState> {
    return userIntentions.refreshEvents
        .switchMap {
          repository
              .getMovies()
              .map { MoviesState(it.result ?: emptyList(), it.fetchAction, it.error) }
        }
  }
}
