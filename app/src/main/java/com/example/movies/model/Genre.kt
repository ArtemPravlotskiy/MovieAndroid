package com.example.movies.model

import kotlinx.serialization.Serializable

@Serializable
data class Genre(
    val id: Int,
    val name: String
)

@Serializable
data class GenresResponse(
    val genres: List<Genre>
)