package com.example.movies.data

import com.example.movies.model.Genre
import com.example.movies.network.MoviesApiService

interface GenresRepository {
    suspend fun getGenres(): List<Genre>
}

class NetworkGenresRepository(
    private val movieApiService: MoviesApiService,
    private val language: String
) : GenresRepository {
    override suspend fun getGenres(): List<Genre> {
        val localizedGenres = movieApiService.getGenres(ApiKey.API_KEY, language).genres
        val englishGenres = movieApiService.getGenres(ApiKey.API_KEY, "en").genres
        val englishGenresMap = englishGenres.associateBy { it.id }

        return localizedGenres.map { localizedGenre ->
            localizedGenre.copy(
                englishName = englishGenresMap[localizedGenre.id]?.name ?: ""
            )
        }
    }
}