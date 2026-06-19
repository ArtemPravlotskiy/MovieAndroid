package com.example.movies.viewModel

import androidx.lifecycle.SavedStateHandle
import com.example.movies.data.MoviesRepository
import com.example.movies.model.Movie
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okio.IOException
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesViewModelTest {

    private val repository: MoviesRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(mapOf("genreId" to "28"))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init load initial page of movies successfully`() = runTest {
        // Given
        val moviePage1 = listOf(mockk<Movie> { coEvery { id } returns 1 })
        coEvery { repository.getMovies("28", 1) } returns moviePage1

        // When
        val viewModel = MoviesViewModel(repository, savedStateHandle)

        // Then
        assertEquals(MoviesUiState.Success(moviePage1), viewModel.moviesUiState.value)
    }

    @Test
    fun `loadMovies pagination appends new movies to existing list`() = runTest {
        // Given
        val movie1 = mockk<Movie> { coEvery { id } returns 1 }
        val movie2 = mockk<Movie> { coEvery { id } returns 2 }

        coEvery { repository.getMovies("28", 1) } returns listOf(movie1)
        coEvery { repository.getMovies("28", 2) } returns listOf(movie2)

        // Инициализация (загрузит страницу 1)
        val viewModel = MoviesViewModel(repository, savedStateHandle)

        // When: вызываем загрузку следующей страницы (страница 2)
        viewModel.loadMovies()

        // Then: списки должны объединиться
        val expectedList = listOf(movie1, movie2)
        assertEquals(MoviesUiState.Success(expectedList), viewModel.moviesUiState.value)
    }

    @Test
    fun `loadMovies failure on page 2 does not erase page 1 data`() = runTest {
        // Given
        val movie1 = mockk<Movie> { coEvery { id } returns 1 }
        coEvery { repository.getMovies("28", 1) } returns listOf(movie1)
        coEvery { repository.getMovies("28", 2) } throws IOException("No internet")

        val viewModel = MoviesViewModel(repository, savedStateHandle)

        // When
        viewModel.loadMovies()

        // Then:
        assertEquals(MoviesUiState.Success(listOf(movie1)), viewModel.moviesUiState.value)
    }
}