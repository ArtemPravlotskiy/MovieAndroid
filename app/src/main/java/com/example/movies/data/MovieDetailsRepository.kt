package com.example.movies.data

import com.example.movies.model.MovieDetails
import com.example.movies.network.MoviesApiService

interface MovieDetailsRepository {
    suspend fun getMovieDetails(movieId: String): MovieDetails
}

class NetworkMovieDetailsRepository(
    private val moviesApiService: MoviesApiService,
    private val settingsRepository: SettingsRepository
) : MovieDetailsRepository {
    override suspend fun getMovieDetails(movieId: String): MovieDetails =
        moviesApiService.getMovieDetails(
            endpoint = "movie/$movieId",
            language = settingsRepository.getSavedLanguage()
        )
}