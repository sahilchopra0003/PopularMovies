package com.demo.popularmovies

import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class ModelTests {
    @Test
    fun `User should see the list of movies when UI is first created`(){
        // Setup
        val moviesList = listOf<Movie>(
            Movie(1, "Dhoni", "Movie on player"),
            Movie(2, "Deadpool", "Action comedy dark comedy"),
            Movie(3, "Avengers", "Sci-Fi")
        )

        val loadingState = MoviesState.INITIAL
        val stateWithData = MoviesState(moviesList, FetchAction.SUCCESSFUL, null)

        val lifecycle = PublishSubject.create<MviLifecycle>()
        val repository = Mockito.mock(CachedRepository::class.java)

        `when`(repository.getMovies())
            .thenReturn(Observable.just(
                FetchEvent(FetchAction.IN_FLIGHT, null, null),
                FetchEvent(FetchAction.SUCCESSFUL, moviesList, null)
            ))

        val observer = MoviesModel.bind(lifecycle, repository)
            .test()

        // Act
        lifecycle.onNext(MviLifecycle.CREATED)

        // Assert

        observer
            .assertValues(loadingState, stateWithData)
    }

    @Test
    fun ``
}