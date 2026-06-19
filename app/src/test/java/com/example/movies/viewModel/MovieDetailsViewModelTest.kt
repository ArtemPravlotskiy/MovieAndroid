package com.example.movies.viewModel

import android.util.Log
import com.example.movies.data.MoviesRepository
import com.example.movies.model.MovieDetails
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import retrofit2.HttpException
import retrofit2.Response
import io.mockk.every
import io.mockk.unmockkStatic

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailsViewModelTest {

    private val repository: MoviesRepository = mockk()
    private lateinit var viewModel: MovieDetailsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        // Подменяем Main диспатчер для тестирования корутин во ViewModel
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Log::class)

        viewModel = MovieDetailsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `getMovieDetails success state flow`() = runTest {
        // Given
        val mockMovie = mockk<MovieDetails>()
        coEvery { repository.getMovieDetails(123) } returns mockMovie

        // When
        viewModel.getMovieDetails("123")

        // Then
        assertEquals(MovieDetailsUiState.Success(mockMovie), viewModel.movieDetailsUiState.value)
    }

    @Test
    fun `loadPlayer success state flow`() = runTest {
        // Given
        val fakeUrl = "https://player.com/video123"
        coEvery { repository.getMoviePlayerUrl("tt12345") } returns fakeUrl

        // When
        viewModel.loadPlayer("tt12345")

        // Then
        assertEquals(PlayerUiState.Success(fakeUrl), viewModel.playerUiState.value)
    }

    @Test
    fun `loadPlayer returns empty or null URL changes state to Error`() = runTest {
        // Given
        coEvery { repository.getMoviePlayerUrl("tt12345") } returns ""

        // When
        viewModel.loadPlayer("tt12345")

        // Then
        assertEquals(PlayerUiState.Error, viewModel.playerUiState.value)
    }
}


@RunWith(Parameterized::class)
class MovieDetailsViewModelErrorsTest(private val exception: Exception) {

    private val repository: MoviesRepository = mockk()
    private lateinit var viewModel: MovieDetailsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MovieDetailsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "Исключение: {0}")
        fun data(): Collection<Array<Any>> {
            val http401 = HttpException(Response.error<Any>(401, "".toResponseBody(null)))
            val http404 = HttpException(Response.error<Any>(404, "".toResponseBody(null)))
            val http500 = HttpException(Response.error<Any>(500, "".toResponseBody(null)))
            val ioException = IOException("No internet connection")

            return listOf(
                arrayOf(http401),
                arrayOf(http404),
                arrayOf(http500),
                arrayOf(ioException)
            )
        }
    }

    @Test
    fun `getMovieDetails handling various errors changes state to Error`() = runTest {
        // Given
        coEvery { repository.getMovieDetails(any()) } throws exception

        // When
        viewModel.getMovieDetails("555")

        // Then
        assertEquals(MovieDetailsUiState.Error, viewModel.movieDetailsUiState.value)
    }
}