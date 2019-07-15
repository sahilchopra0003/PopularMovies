package com.demo.popularmovies

import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MoviesViewTests {

    @Spy
    lateinit var view: SpyableMoviesView

    @Test
    fun `render initial state`() {
        //Setup
        val state = MoviesState.INITIAL

        // Act
        view.render(state)

        // Assert
        verify(view).showLoadingView(true)
        verify(view).showError("", false)
        verify(view).showNoDataLabel(false)
    }

    @Test
    fun `render fetch success state`() {
        // Setup
        val moviesList = listOf<Movie>(
            Movie(1, "Dhoni", "Movie on player"),
            Movie(2, "Deadpool", "Action comedy dark comedy"),
            Movie(3, "Avengers", "Sci-Fi")
        )
        val successState = MoviesState(moviesList, FetchAction.SUCCESSFUL, null)

        // Act
        view.render(successState)

        // Assert
        verify(view).showLoadingView(false)
        verify(view).showError("", false)
        verify(view).showNoDataLabel(false)
    }

    @Test
    fun `render failed state`() {
        // Setup
        val errorMsg = "Oops! Something went wrong!"
        val failedState = MoviesState(emptyList(), FetchAction.FAILED, errorMsg)

        // Act
        view.render(failedState)

        // Assert
        verify(view).showLoadingView(false)
        verify(view).showError(errorMsg, true)
        verify(view).showNoDataLabel(false)
    }
}
