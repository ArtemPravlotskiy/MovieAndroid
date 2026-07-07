package com.example.movies.viewModel

import com.example.movies.data.MoviesRepository
import com.example.movies.data.SettingsRepository
import com.example.movies.model.Movie
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private val moviesRepository: MoviesRepository = mockk(relaxed = true)
    private val settingsRepository: SettingsRepository = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `observeFavorites success updates state to Success`() = runTest {
        // Given
        val mockList = listOf(mockk<Movie>(), mockk<Movie>())
        every { moviesRepository.getFavoriteMoviesStream() } returns flowOf(mockList)

        // When
        val viewModel = FavoritesViewModel(moviesRepository, settingsRepository)

        // Then
        assertTrue(viewModel.favoritesUiState.value is MoviesUiState.Success)
        val successState = viewModel.favoritesUiState.value as MoviesUiState.Success
        assertEquals(mockList, successState.movies)
    }

    @Test
    fun `observeFavorites stream error updates state to Error`() = runTest {
        // Given
        every { moviesRepository.getFavoriteMoviesStream() } returns flow {
            throw IOException("Database corruption or read error")
        }

        // When
        val viewModel = FavoritesViewModel(moviesRepository, settingsRepository)

        // Then
        assertTrue(viewModel.favoritesUiState.value is MoviesUiState.Error)
    }

    @Test
    fun `loadFavorites handles exceptions safely`() = runTest {
        // Given
        val mockMovie = mockk<Movie> { every { id } returns 999 }
        every { moviesRepository.getFavoriteMoviesStream() } returns flowOf(listOf(mockMovie))
        coEvery { moviesRepository.getMovieDetails(999) } throws RuntimeException("Network timeout")

        val viewModel = FavoritesViewModel(moviesRepository, settingsRepository)

        // When & Then (тест не должен выбросить краш наружу)
        viewModel.loadFavorites()
    }
}