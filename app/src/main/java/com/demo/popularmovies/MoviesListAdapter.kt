package com.demo.popularmovies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MoviesListAdapter(
    val moviesList: ArrayList<Movie>
) : RecyclerView.Adapter<MoviesListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    android.R.layout.simple_list_item_2,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int {
        return moviesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = moviesList.get(position)
        holder.id.text = movie.id.toString()
        holder.title.text = movie.title
        holder.overview.text = movie.overview
    }

    fun refreshList(newList: ArrayList<Movie>) {
        moviesList.clear()
        moviesList.addAll(newList)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(android.R.id.text1)
        val id = itemView.findViewById<TextView>(android.R.id.text2)
        val overview = itemView.findViewById<TextView>(android.R.id.text2)
    }
}
