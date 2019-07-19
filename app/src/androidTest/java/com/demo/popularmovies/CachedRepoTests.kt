package com.demo.popularmovies

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.reactivex.Observable
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.net.ConnectException

@RunWith(AndroidJUnit4::class)
class CachedRepoTests {

    @Test
    fun When_db_returns_no_data_and_api_failure() {
        // Setup
        val context = InstrumentationRegistry.getInstrumentation().context
        val errorMsg = "Oops! Something went wrong."

        val inFlightEvent = FetchEvent<List<Movie>>(FetchAction.IN_FLIGHT, emptyList(), null)
        val failedEvent = FetchEvent<List<Movie>>(FetchAction.FAILED, null, errorMsg)

        val apiService = Mockito.mock(ApiService::class.java)
        Mockito.`when`(apiService.getMovies())
            .thenReturn(Observable.error(ConnectException(errorMsg)))

        val appDatabase = Room
            .inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .build()

        val observer = CachedRepositoryImpl(
            apiService,
            appDatabase
        )
            .getMovies()
            .test()

        // Assert
        observer.assertValues(inFlightEvent, failedEvent)
    }

    @Test
    fun when_db_returns_data_and_api_fails() {
        // Setup
        val movieList = arrayListOf<Movie>(
            Movie(1, "Avengers", "Action Comedy"),
            Movie(2, "Avengers", "Action Comedy"),
            Movie(3, "Avengers", "Action Comedy")
        )
        val errorMsg = "Oops! Something went wrong."

        val inFlightEvent = FetchEvent(FetchAction.IN_FLIGHT, movieList, null)
        val failedEvent = FetchEvent(FetchAction.FAILED, null, errorMsg)

        val apiService = Mockito.mock(ApiService::class.java)
        Mockito
            .`when`(apiService.getMovies())
            .thenReturn(Observable.error(ConnectException(errorMsg)))

        val appDatabase = Room
            .inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().context,
                AppDatabase::class.java
            )
            .build()

        appDatabase.movieDao().insertMovies(movieList)
        val observer = CachedRepositoryImpl(apiService, appDatabase)
            .getMovies()
            .test()

        // Assert
        observer.assertValues(inFlightEvent, failedEvent)
    }

    @Test
    fun when_db_does_not_return_data_and_api_success() {
        // Setup
        val movieList = arrayListOf<Movie>(
            Movie(1, "Avengers", "Action Comedy"),
            Movie(2, "Avengers", "Action Comedy"),
            Movie(3, "Avengers", "Action Comedy")
        )

        val inFlightEvent = FetchEvent<List<Movie>>(FetchAction.IN_FLIGHT, emptyList(), null)
        val successEvent = FetchEvent<List<Movie>>(FetchAction.SUCCESSFUL, movieList, null)

        val apiService = Mockito.mock(ApiService::class.java)
        Mockito.`when`(apiService.getMovies())
            .thenReturn(Observable.just(MoviesResponse(movieList)))

        val appDatabase = Room
            .inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().context,
                AppDatabase::class.java
            )
            .build()

        val observer = CachedRepositoryImpl(apiService, appDatabase)
            .getMovies()
            .test()

        val dbObserver = appDatabase.movieDao().getAll().test()

        // Assert
        observer.assertValues(inFlightEvent, successEvent)
        dbObserver.assertValue(movieList)
    }

    @Test
    fun when_db_returns_data_and_api_success() {
        // Setup
        val movieList = arrayListOf<Movie>(
            Movie(1, "Avengers", "Action Comedy"),
            Movie(2, "Avengers", "Action Comedy"),
            Movie(3, "Avengers", "Action Comedy")
        )

        val inFlightEvent = FetchEvent(FetchAction.IN_FLIGHT, movieList, null)
        val successEvent = FetchEvent(FetchAction.SUCCESSFUL, movieList, null)

        val apiService = Mockito.mock(ApiService::class.java)
        Mockito.`when`(apiService.getMovies())
            .thenReturn(Observable.just(MoviesResponse(movieList)))

        val appDatabase = Room
            .inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().context,
                AppDatabase::class.java
            )
            .build()

        appDatabase
            .movieDao()
            .insertMovies(movieList)

        val observer = CachedRepositoryImpl(apiService, appDatabase)
            .getMovies()
            .test()

        val dbObserver = appDatabase
            .movieDao()
            .getAll()
            .test()

        // Assert
        observer.assertValues(inFlightEvent, successEvent)
        dbObserver.assertValue(movieList)
    }
}
