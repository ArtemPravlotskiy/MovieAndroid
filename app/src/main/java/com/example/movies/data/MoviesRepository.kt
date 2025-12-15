package com.example.movies.data

import com.example.movies.model.Movie
import com.example.movies.network.MoviesApiService

interface MoviesRepository {
    suspend fun getMovies(genreId: String, page: Int): List<Movie>
    suspend fun searchMovies(query: String): List<Movie>
}

class NetworkMoviesRepository(
    private val movieApiService: MoviesApiService,
    private val settingsRepository: SettingsRepository
) : MoviesRepository {
    override suspend fun getMovies(genreId: String, page: Int): List<Movie> =
        movieApiService.getMovies(
            genreId = genreId,
            page = page,
            language = settingsRepository.getSavedLanguage()
        ).movies

    override suspend fun searchMovies(query: String): List<Movie> =
        movieApiService.searchMovies(
            query = query,
            language = settingsRepository.getSavedLanguage()
        ).movies
}