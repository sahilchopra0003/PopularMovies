package com.demo.popularmovies

import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class ModelTests {
  lateinit var lifecycleEvents: PublishSubject<MviLifecycle>
  lateinit var refreshEvents: PublishSubject<Unit>
  lateinit var userIntentions: MoviesIntention
  lateinit var repository: CachedRepository
  lateinit var previousStates: BehaviorSubject<MoviesState>
  lateinit var observer: TestObserver<MoviesState>
  lateinit var moviesList: List<Movie>

  @Before
  fun setup() {
    lifecycleEvents = PublishSubject.create()
    refreshEvents = PublishSubject.create()
    userIntentions = MoviesIntention(refreshEvents)
    repository = Mockito.mock(CachedRepository::class.java)
    previousStates = BehaviorSubject.create()

    moviesList = listOf<Movie>(
        Movie(1, "Dhoni", "Movie on player"),
        Movie(2, "Deadpool", "Action comedy dark comedy"),
        Movie(3, "Avengers", "Sci-Fi")
    )

    observer = MoviesModel
        .bind(lifecycleEvents, userIntentions, repository, previousStates)
        .doOnNext { previousStates.onNext(it) }
        .test()
  }

  @Test
  fun `User should see the list of movies when UI is first created`() {
    // Setup
    val loadingState = MoviesState.INITIAL
    val stateWithData = MoviesState(moviesList, FetchAction.SUCCESSFUL, null)

    `when`(repository.getMovies())
        .thenReturn(
            Observable.just(
                FetchEvent(FetchAction.IN_FLIGHT, null, null),
                FetchEvent(FetchAction.SUCCESSFUL, moviesList, null)
            )
        )

    // Act
    lifecycleEvents.onNext(MviLifecycle.CREATED)

    // Assert
    observer
        .assertValues(loadingState, stateWithData)
  }

  @Test
  fun `User should see the error when no internet`() {
    // Setup
    val errorMsg = "Oops! Something went wrong."

    val loadingState = MoviesState(emptyList(), FetchAction.IN_FLIGHT, null)
    val errorState = MoviesState(emptyList(), FetchAction.FAILED, errorMsg)

    Mockito.`when`(repository.getMovies())
        .thenReturn(Observable.just(FetchEvent(FetchAction.IN_FLIGHT, null, null),
            FetchEvent(FetchAction.FAILED, null, errorMsg)))

    // Act
    lifecycleEvents.onNext(MviLifecycle.CREATED)

    // Assert
    observer.assertValues(loadingState, errorState)
  }

  @Test
  fun `User should see the same data on configuration changes`() {
    // Setup
    Mockito
        .`when`(repository.getMovies())
        .thenReturn(Observable.just(
            FetchEvent(FetchAction.IN_FLIGHT, emptyList(), null),
            FetchEvent(FetchAction.SUCCESSFUL, moviesList, null)
        ))

    val loadingState = MoviesState.INITIAL
    val stateWithData = MoviesState(moviesList, FetchAction.SUCCESSFUL, null)

    // Act
    lifecycleEvents.onNext(MviLifecycle.CREATED)
    lifecycleEvents.onNext(MviLifecycle.RESUMED)

    // Assert
    observer.assertValues(loadingState, stateWithData, stateWithData)
  }

  @Test
  fun `User should see the refreshed list when clicks on retry`() {
    // Setup
    val errorMsg = "Oops! Something went wrong."
    Mockito
        .`when`(repository.getMovies())
        .thenReturn(Observable.just(
            FetchEvent(FetchAction.IN_FLIGHT, null, null),
            FetchEvent(FetchAction.FAILED, null, errorMsg)
        ))
        .thenReturn(Observable.just(
            FetchEvent(FetchAction.IN_FLIGHT, null, null),
            FetchEvent(FetchAction.SUCCESSFUL, moviesList, null)
        ))

    val loadingState = MoviesState.INITIAL
    val errorState = MoviesState(emptyList(), FetchAction.FAILED, errorMsg)
    val stateWithData = MoviesState(moviesList, FetchAction.SUCCESSFUL, null)

    // Act
    lifecycleEvents.onNext(MviLifecycle.CREATED)
    refreshEvents.onNext(Unit)

    // Assert
    observer.assertValues(loadingState, errorState, loadingState, stateWithData)
  }
}
