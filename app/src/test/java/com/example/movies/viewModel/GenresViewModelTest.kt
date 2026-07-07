package com.example.movies.viewModel

import android.util.Log
import com.example.movies.data.GenresRepository
import com.example.movies.model.GenreDTO
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
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

@OptIn(ExperimentalCoroutinesApi::class)
class GenresViewModelTest {

    private val repository: GenresRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `init automatically fetches genres successfully`() = runTest {
        // Given
        val mockGenres = listOf(
            GenreDTO(id = 1, name = "Action", imageName = ""),
            GenreDTO(id = 2, name = "Comedy", imageName = "")
        )
        coEvery { repository.getGenres() } returns mockGenres

        // When
        val viewModel = GenresViewModel(repository)

        // Then
        assertEquals(GenresUiState.Success(mockGenres), viewModel.genresUiState.value)
    }
}

@RunWith(Parameterized::class)
class GenresViewModelErrorsTest(private val exception: Exception) {

    private val repository: GenresRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "Ошибка жанров: {0}")
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf(IOException("Network broken")),
            arrayOf(HttpException(Response.error<Any>(500, "".toResponseBody(null)))),
            arrayOf(RuntimeException("Unexpected global crash"))
        )
    }

    @Test
    fun `getGenres handles errors and updates state to Error`() = runTest {
        coEvery { repository.getGenres() } throws exception

        val viewModel = GenresViewModel(repository)
        viewModel.getGenres()

        assertEquals(GenresUiState.Error, viewModel.genresUiState.value)
    }
}