package com.demo.popularmovies

import io.reactivex.Observable
import org.junit.Test
import org.mockito.Mockito
import java.net.ConnectException

class CachedRepositoryTests {

    @Test
    fun `Return successful fetch event in case of api call success`() {
        // Setup
        val inFlightEvent = FetchEvent<List<Movie>>(FetchAction.IN_FLIGHT, null, null)
        val successfulEvent = FetchEvent<List<Movie>>(FetchAction.SUCCESSFUL, emptyList(), null)

        val apiService = Mockito.mock(ApiService::class.java)
        Mockito.`when`(apiService.getMovies())
            .thenReturn(Observable.just(MoviesResponse(emptyList())))

        val observer = CachedRepositoryImpl(apiService)
            .getMovies()
            .test()

        // Assert
        observer.assertValues(inFlightEvent, successfulEvent)
    }

    @Test
    fun `Return failed fetch event in case of api call error`() {
        // Setup
        val errorMsg = "Oops! Something went wrong."
        val inFlightEvent = FetchEvent<List<Movie>>(FetchAction.IN_FLIGHT, null, null)
        val errorEvent = FetchEvent<List<Movie>>(FetchAction.FAILED, null, errorMsg)

        val apiService = Mockito.mock(ApiService::class.java)
        Mockito.`when`(apiService.getMovies())
            .thenReturn(Observable.error(ConnectException(errorMsg)))

        val observer = CachedRepositoryImpl(apiService)
            .getMovies()
            .test()

        // Assert
        observer.assertValues(inFlightEvent, errorEvent)
    }
}
