package com.example.movies.data

import com.example.movies.model.Genre
import com.example.movies.network.MoviesApiService

interface GenresRepository {
    suspend fun getGenres(): List<Genre>
}

class NetworkGenresRepository(
    private val movieApiService: MoviesApiService
) : GenresRepository {
    override suspend fun getGenres(): List<Genre> = movieApiService.getGenres(ApiKey.API_KEY).genres
}