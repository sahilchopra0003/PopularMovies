package com.demo.popularmovies

interface MoviesView {
    fun render(state: MoviesState) {
        showLoadingView(true)
        showNoDataLabel(false)
        showError("", false)
    }

    fun showLoadingView(show: Boolean)

    fun showNoDataLabel(show: Boolean)

    fun showError(errorMsg: String, show: Boolean)

    fun refreshList(newList: List<Movie>)

}
