package com.demo.popularmovies

data class FetchEvent<out T>(
    val fetchAction: FetchAction,
    val result: T?,
    val error: String?
)
