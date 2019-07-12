package com.demo.popularmovies

data class MoviesState(
    val movies: List<Movie>,
    val fetchAction: FetchAction,
    val error: String?
) {
    companion object {
        val INITIAL = MoviesState(emptyList(), FetchAction.IN_FLIGHT, null)
    }
}
