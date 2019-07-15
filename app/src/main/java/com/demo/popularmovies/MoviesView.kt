package com.demo.popularmovies

interface MoviesView {
    fun render(state: MoviesState) {
        when (state.fetchAction) {
            FetchAction.IN_FLIGHT -> {
                showLoadingView(true)
                showError("", false)
                showNoDataLabel(false)
                refreshList(state.movies)
            }

            FetchAction.SUCCESSFUL -> {
                showLoadingView(false)
                showError("", false)
                if (state.movies.isEmpty()) {
                    showNoDataLabel(true)
                } else {
                    showNoDataLabel(false)
                }
                refreshList(state.movies)
            }

            FetchAction.FAILED -> {
                showLoadingView(false)
                showError(state.error ?: "", true)
                showNoDataLabel(false)
                refreshList(state.movies)
            }
        }
    }

    fun showLoadingView(show: Boolean)

    fun showNoDataLabel(show: Boolean)

    fun showError(errorMsg: String, show: Boolean)

    fun refreshList(newList: List<Movie>)

}
