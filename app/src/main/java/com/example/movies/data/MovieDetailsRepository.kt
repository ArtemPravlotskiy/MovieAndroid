package com.example.movies.data

import com.example.movies.model.MovieDetails
import com.example.movies.network.MoviesApiService

interface MovieDetailsRepository {
    suspend fun getMovieDetails(movieId: String): MovieDetails
}

class NetworkMovieDetailsRepository(
    private val moviesApiService: MoviesApiService,
    private val language: String
) : MovieDetailsRepository {
    override suspend fun getMovieDetails(movieId: String): MovieDetails =
        moviesApiService.getMovieDetails(movieId = movieId, key = ApiKey.API_KEY, language = language)
}