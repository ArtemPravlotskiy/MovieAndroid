package com.example.movies.data

import android.util.Log
import com.example.movies.model.ChatRequest
import com.example.movies.model.ChatResponse
import com.example.movies.model.Movie
import com.example.movies.model.MovieDetails
import com.example.movies.network.ExternalIdApiService
import com.example.movies.network.MoviesApiService
import com.example.movies.network.PlayerApiService

interface MoviesRepository {
    suspend fun getMovies(genreId: String, page: Int): List<Movie>
    suspend fun searchMovies(query: String): List<Movie>
    suspend fun vectorSearch(query: String): List<Movie>
    suspend fun getMovieDetails(movieId: Int): MovieDetails
    suspend fun chatWithAi(request: ChatRequest): ChatResponse

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

    override suspend fun vectorSearch(query: String): List<Movie> =
        movieApiService.vectorSearch(query = query)

    override suspend fun getMovieDetails(movieId: Int): MovieDetails =
        movieApiService.getMovieDetails(
            endpoint = "movie/$movieId",
            language = settingsRepository.getSavedLanguage()
        )

    override suspend fun chatWithAi(request: ChatRequest): ChatResponse =
        movieApiService.chatWithAi(request)

    override suspend fun getExternalId(imdbId: String): Int? =
        externalIdApiService.getExternalId(imdbId).docs.firstOrNull()?.id

    override suspend fun getMoviePlayerUrl(imdbId: String): String? {
        Log.d("MoviesRepository", "getMoviePlayerUrl called for imdbId: $imdbId")
        if (imdbId.isBlank()) {
            Log.w("MoviesRepository", "imdbId is blank, returning null")
            return null
        }
        val token = "eyJhbGciOiJIUzI1NiJ9.eyJ3ZWJTaXRlIjoiNzQyIiwiaXNzIjoiYXBpLXdlYm1hc3RlciIsInN1YiI6Ijg1MyIsImlhdCI6MTc3NTcyOTY2NiwianRpIjoiMThlNjBlMzUtZTZiZS00ZWQzLTg3ODQtNTQzYTE3NTQwYWRhIiwic2NvcGUiOiJETEUifQ.A1IC5EXu4yM08qBMAnYgM82FpiuTMN__-f7Q23D0-iQ"
        val url = "https://api3.rstprgapipt.com/balancer-api/iframe?imdb=$imdbId&lang_order=rus&lang_order=eng&token=$token"
        Log.d("MoviesRepository", "Generated player URL: $url")
        return url
    }
}
