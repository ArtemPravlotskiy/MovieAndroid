package com.example.movies.viewModel

import com.example.movies.data.MoviesRepository
import com.example.movies.model.MovieDetails
import com.example.movies.model.Movie
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val repository: MoviesRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleSearchMode switches vector search flag`() {
        val viewModel = SearchViewModel(repository)
        assertFalse(viewModel.isVectorSearch.value)

        viewModel.toggleSearchMode()
        assertTrue(viewModel.isVectorSearch.value)
    }

    @Test
    fun `searchMovies executes normal search after 1 second debounce`() = runTest {
        val viewModel = SearchViewModel(repository)
        val expectedMovies = listOf(mockk<Movie>())
        coEvery { repository.searchMovies("Avatar") } returns expectedMovies

        viewModel.searchMovies("Avatar")

        advanceTimeBy(500)
        assertTrue(viewModel.searchResult.value.isEmpty())

        advanceTimeBy(501)
        assertEquals(expectedMovies, viewModel.searchResult.value)
    }

    @Test
    fun `searchMovies with new query cancels previous job`() = runTest {
        val viewModel = SearchViewModel(repository)
        coEvery { repository.searchMovies("Matrix") } returns listOf(mockk())
        coEvery { repository.searchMovies("Interstellar") } returns listOf(mockk())

        viewModel.searchMovies("Matrix")
        advanceTimeBy(500)

        viewModel.searchMovies("Interstellar")
        advanceTimeBy(1001)

        coEvery { repository.searchMovies("Interstellar") }
    }

    @Test
    fun `searchMovies in vector mode fetches brief results and requests full details`() = runTest {
        val viewModel = SearchViewModel(repository)
        viewModel.toggleSearchMode()

        val briefMovie = mockk<Movie> { every { id } returns 77 }
        val mockDetails = mockk<MovieDetails> {
            every { id } returns 77
            every { title } returns "Семантический Хит"
            every { overview } returns "Описание"
            every { voteAverage } returns 8.5
            every { posterPath } returns "/path.jpg"
            every { genres } returns emptyList()
        }

        coEvery { repository.vectorSearch("умное кино") } returns listOf(briefMovie)
        coEvery { repository.getMovieDetails(77) } returns mockDetails

        viewModel.searchMovies("умное кино")
        advanceTimeBy(1001)

        val results = viewModel.searchResult.value
        assertEquals(1, results.size)
        assertEquals("Семантический Хит", results[0].title)
        assertEquals(8.5, results[0].voteAverage, 0.01)
    }
}