package com.demo.popularmovies

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Single

@Dao
interface MovieDao {

    @Query("SELECT * FROM Movie")
    fun getAll(): Single<List<Movie>>

    @Insert
    fun insertMovies(movieList: List<Movie>)

}
