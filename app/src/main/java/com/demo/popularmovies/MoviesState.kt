package com.demo.popularmovies

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MoviesState(
    val movies: List<Movie>,
    val fetchAction: FetchAction,
    val error: String?
) : Parcelable {
    companion object {
        val INITIAL = MoviesState(emptyList(), FetchAction.IN_FLIGHT, null)
    }
}
