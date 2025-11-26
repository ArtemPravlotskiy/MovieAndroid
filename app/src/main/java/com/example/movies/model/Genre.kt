package com.example.movies.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Genre(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    // -val imagePath: String?
    val englishName: String = ""
)

//TODO: New genre usable
// GenreDTO (-englishname)

@Serializable
data class GenresResponse(
    val genres: List<Genre>
)