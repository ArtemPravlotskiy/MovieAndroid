package com.example.movies.data

import android.util.Log
import androidx.glance.appwidget.updateAll
import com.example.movies.MovieWidget
import com.example.movies.model.ChatRequest
import com.example.movies.model.ChatResponse
import com.example.movies.model.Movie
import com.example.movies.model.MovieDetails
import com.example.movies.network.ExternalIdApiService
import com.example.movies.network.MoviesApiService
import com.example.movies.network.PlayerApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

interface MoviesRepository {
    suspend fun getMovies(genreId: String, page: Int): List<Movie>
    suspend fun searchMovies(query: String): List<Movie>
    suspend fun vectorSearch(query: String): List<Movie>
    suspend fun getMovieDetails(movieId: Int): MovieDetails
    suspend fun chatWithAi(request: ChatRequest): ChatResponse
    suspend fun getExternalId(imdbId: String): Int?
    suspend fun getMoviePlayerUrl(imdbId: String): String?
    fun getFavoriteMoviesStream(): Flow<List<Movie>>
    suspend fun insertFavorite(movie: Movie)
    suspend fun insertFavoriteDetails(details: MovieDetails)
    suspend fun deleteFavorite(movie: Movie)
    suspend fun isFavorite(id: Int): Boolean
    suspend fun getLocalMovie(id: Int): Movie?
}

class NetworkMoviesRepository(
    private val movieApiService: MoviesApiService,
    private val externalIdApiService: ExternalIdApiService,
    private val playerApiService: PlayerApiService,
    private val settingsRepository: SettingsRepository,
    private val movieDao: MovieDao
) : MoviesRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private fun notifyWidget() {
        repositoryScope.launch {
            MovieWidget().updateAll(settingsRepository.context)
        }
    }

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

    override suspend fun getMovieDetails(movieId: Int): MovieDetails {
        return try {
            val details = movieApiService.getMovieDetails(
                endpoint = "movie/$movieId",
                language = settingsRepository.getSavedLanguage()
            )
            if (isFavorite(movieId)) {
                // ВАЖНО: insertFavoriteDetails не должен вызывать notifyWidget(),
                // чтобы не было бесконечного цикла при обновлении виджета
                movieDao.insertMovie(details.asEntity())
            }
            details
        } catch (e: Exception) {
            val local = movieDao.getMovieById(movieId)
            local?.asDetailsModel() ?: throw e
        }
    }

    override suspend fun chatWithAi(request: ChatRequest): ChatResponse =
        movieApiService.chatWithAi(request)

    override suspend fun getExternalId(imdbId: String): Int? =
        externalIdApiService.getExternalId(imdbId).docs.firstOrNull()?.id

    override suspend fun getMoviePlayerUrl(imdbId: String): String? {
        val token = "eyJhbGciOiJIUzI1NiJ9.eyJ3ZWJTaXRlIjoiNzQyIiwiaXNzIjoiYXBpLXdlYm1hc3RlciIsInN1YiI6Ijg1MyIsImlhdCI6MTc3NTcyOTY2NiwianRpIjoiMThlNjBlMzUtZTZiZS00ZWQzLTg3ODQtNTQzYTE3NTQwYWRhIiwic2NvcGUiOiJETEUifQ.A1IC5EXu4yM08qBMAnYgM82FpiuTMN__-f7Q23D0-iQ"
        return "https://api3.rstprgapipt.com/balancer-api/iframe?imdb=$imdbId&lang_order=rus&lang_order=eng&token=$token"
    }

    override fun getFavoriteMoviesStream(): Flow<List<Movie>> =
        movieDao.getAllFavorites().map { entities -> entities.map { it.asDomainModel() } }

    override suspend fun insertFavorite(movie: Movie) {
        movieDao.insertMovie(movie.asEntity())
        notifyWidget()
    }

    override suspend fun insertFavoriteDetails(details: MovieDetails) {
        movieDao.insertMovie(details.asEntity())
        // Здесь не вызываем notifyWidget(), так как это фоновое обновление данных
    }

    override suspend fun deleteFavorite(movie: Movie) {
        movieDao.deleteMovieById(movie.id)
        notifyWidget()
    }

    override suspend fun isFavorite(id: Int): Boolean {
        return movieDao.getMovieById(id) != null
    }

    override suspend fun getLocalMovie(id: Int): Movie? {
        return movieDao.getMovieById(id)?.asDomainModel()
    }
}
