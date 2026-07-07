package com.example.movies.data

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.example.movies.MovieWidget
import com.example.movies.model.Movie
import com.example.movies.model.MovieDetails
import com.example.movies.network.ExternalIdApiService
import com.example.movies.network.MoviesApiService
import com.example.movies.network.PlayerApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesRepositoryTest {

    private val movieApiService: MoviesApiService = mockk()
    private val externalIdApiService: ExternalIdApiService = mockk()
    private val playerApiService: PlayerApiService = mockk()
    private val settingsRepository: SettingsRepository = mockk()
    private val movieDao: MovieDao = mockk(relaxed = true)

    private val mockContext: Context = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var moviesRepository: NetworkMoviesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { settingsRepository.context } returns mockContext
        every { settingsRepository.getSavedLanguage() } returns "ru"

        mockkStatic("androidx.glance.appwidget.GlanceAppWidgetKt")
        coEvery { any<androidx.glance.appwidget.GlanceAppWidget>().updateAll(any()) } coAnswers { }

        moviesRepository = NetworkMoviesRepository(
            movieApiService,
            externalIdApiService,
            playerApiService,
            settingsRepository,
            movieDao
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic("androidx.glance.appwidget.GlanceAppWidgetKt")
    }

    @Test
    fun `getMovieDetails returns data from API and saves to DB if movie is favorite`() = runTest {

        val movieId = 550

        val sampleDetails = MovieDetails(
            id = movieId,
            title = "Fight Club",
            overview = "An insomniac office worker...",
            posterPath = "/posterPath.jpg",
            backdropPath = "/backdropPath.jpg",
            genres = emptyList(),
            imdbId = "imdbId",
            releaseDate = "Date",
            runtime = 120,
            tagline = "tag",
            voteAverage = 10.0
        )

        coEvery { movieApiService.getMovieDetails("movie/$movieId", "ru") } returns sampleDetails
        coEvery { movieDao.getMovieById(movieId) } returns mockk(relaxed = true)

        val result = moviesRepository.getMovieDetails(movieId)

        assertEquals(sampleDetails, result)
        coVerify(exactly = 1) { movieDao.insertMovie(any()) }
    }

    @Test
    fun `getMovieDetails falls back to local DB when API call fails`() = runTest {

        val movieId = 550

        val sampleEntity = MovieEntity(
            id = movieId,
            title = "Fight Club Local",
            overview = "An insomniac...",
            posterPath = "/path.jpg",
            voteAverage = 10.0
        )

        coEvery { movieApiService.getMovieDetails(any(), any()) } throws Exception("Network Error")
        coEvery { movieDao.getMovieById(movieId) } returns sampleEntity

        val result = moviesRepository.getMovieDetails(movieId)

        assertEquals(movieId, result.id)
        assertEquals("Fight Club Local", result.title)
        coVerify(exactly = 0) { movieDao.insertMovie(any()) }
    }

    @Test
    fun `insertFavorite saves movie to DB and notifies widget`() = runTest {

        val sampleMovie = Movie(
            id = 123,
            title = "Inception",
            posterPath = "/inception.jpg"
        )

        moviesRepository.insertFavorite(sampleMovie)

        coVerify(exactly = 1) { movieDao.insertMovie(any()) }
        coVerify(exactly = 1) { any<MovieWidget>().updateAll(mockContext) }
    }
}