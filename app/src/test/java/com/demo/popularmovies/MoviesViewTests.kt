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
}
