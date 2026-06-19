package com.example.movies.data

import android.content.Context
import android.content.res.Resources
import com.example.movies.model.Genre
import com.example.movies.model.GenreDTO
import com.example.movies.model.GenresResponse
import com.example.movies.network.MoviesApiService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import java.io.ByteArrayInputStream
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GenresRepositoryTest {

    private val movieApiService: MoviesApiService = mockk()
    private val settingsRepository: SettingsRepository = mockk()

    private val mockContext: Context = mockk()
    private val mockResources: Resources = mockk()

    private lateinit var genresRepository: NetworkGenresRepository

    @Before
    fun setUp() {
        every { settingsRepository.context } returns mockContext
        every { mockContext.resources } returns mockResources

        genresRepository = NetworkGenresRepository(movieApiService, settingsRepository)
    }

    @Test
    fun `getGenres fetches localized genres and maps them correctly with matching jsonIds`() = runTest {
        val language = "ru"
        every { settingsRepository.getSavedLanguage() } returns language

        val apiGenre = mockk<Genre> {
            every { id } returns 28
            every { name } returns "Экшен"
        }

        val mockApiResponse = mockk<GenresResponse> {
            every { genres } returns listOf(apiGenre)
        }
        coEvery { movieApiService.getGenres(language = language) } returns mockApiResponse

        val fakeJson = """
            [
                {"id": 28, "name": "Action"}
            ]
        """.trimIndent()
        val fakeInputStream = ByteArrayInputStream(fakeJson.toByteArray())
        every { mockResources.openRawResource(any()) } returns fakeInputStream

        val result = genresRepository.getGenres()

        assertEquals(1, result.size)
        val genreDto = result.first()
        assertEquals(28, genreDto.id)
        assertEquals("Экшен", genreDto.name)
        assertEquals("action", genreDto.imageName)
    }
}