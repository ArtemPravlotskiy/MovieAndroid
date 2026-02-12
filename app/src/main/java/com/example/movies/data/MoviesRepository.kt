package com.example.movies.data

import com.example.movies.model.Movie
import com.example.movies.model.MovieDetails
import com.example.movies.network.ExternalIdApiService
import com.example.movies.network.MoviesApiService
import com.example.movies.network.PlayerApiService

interface MoviesRepository {
    suspend fun getMovies(genreId: String, page: Int): List<Movie>
    suspend fun searchMovies(query: String): List<Movie>
    suspend fun getMovieDetails(movieId: Int): MovieDetails

    suspend fun getExternalId(imdbId: String): Int?

    suspend fun getMoviePlayerUrl(imdbId: String): String?
}

class NetworkMoviesRepository(
    private val movieApiService: MoviesApiService,
    private val externalIdApiService: ExternalIdApiService,
    private val playerApiService: PlayerApiService,
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

    override suspend fun getMovieDetails(movieId: Int): MovieDetails =
        movieApiService.getMovieDetails(
            endpoint = "movie/$movieId",
            language = settingsRepository.getSavedLanguage()
        )

    override suspend fun getExternalId(imdbId: String): Int? =
        externalIdApiService.getExternalId(imdbId).docs.firstOrNull()?.id

    override suspend fun getMoviePlayerUrl(imdbId: String): String? {
        return try {
            val kinopoiskId = getExternalId(imdbId) ?: return null
            val players = playerApiService.getPlayers(kinopoiskId)
            players
                .firstOrNull { it.name.equals("collaps", ignoreCase = true) }
                ?.iframe
        } catch (e: Exception) {
            null
        }
    }
}