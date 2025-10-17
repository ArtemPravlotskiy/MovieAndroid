package com.example.movies.model

import kotlinx.serialization.Serializable

@Serializable
data class Genre(
    val id: String,
    val name: String
)
