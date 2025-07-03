package com.example.movieapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieResponse(
    val page: Int,
    val results: List<Movie>,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_results")
    val totalResults: Int
)

@Serializable
data class Movie(
    val id: Int,
    val title: String,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("overview")
    val overview: String,
    @SerialName("release_date")
    val releaseDate: String = "N/A",
    @SerialName("vote_average")
    val voteAverage: Double = 0.0
)