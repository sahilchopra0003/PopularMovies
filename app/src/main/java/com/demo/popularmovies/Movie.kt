package com.demo.popularmovies

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Movie(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String
) : Parcelable
