package com.example.movies.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val id: Int,
    val title: String,
    @SerialName("original_title")
    val originalTitle: String? = null,
    @SerialName("original_language")
    val originalLanguage: String? = null,
    val overview: String = "",
    @SerialName("release_date")
    val releaseDate: String? = null,
    val popularity: Double = 0.0,
    @SerialName("vote_average")
    val voteAverage: Double = 0.0,
    @SerialName("vote_count")
    val voteCount: Int = 0,
    val adult: Boolean = false,
    val video: Boolean = false,
    @SerialName("genre_ids")
    val genreIds: List<Int> = emptyList(),
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null
)


@Serializable
data class MoviesResponse(
    val page: Int,
    @SerialName("results")
    val movies: List<Movie>
)