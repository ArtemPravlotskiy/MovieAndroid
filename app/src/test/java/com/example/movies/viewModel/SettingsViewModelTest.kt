package com.example.movies.viewModel

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatDelegate
import androidx.glance.appwidget.updateAll
import com.example.movies.MovieWidget
import com.example.movies.ReminderManager
import com.example.movies.data.MoviesRepository
import com.example.movies.data.SettingsRepository
import com.example.movies.data.TextScale
import com.example.movies.data.Theme
import com.example.movies.model.Movie
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val settingsRepository: SettingsRepository = mockk()
    private val moviesRepository: MoviesRepository = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockContext: Context = mockk(relaxed = true)
    private val mockResources: Resources = mockk(relaxed = true)
    private val mockConfiguration = Configuration()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(AppCompatDelegate::class)
        mockkStatic(MovieWidget::class)
        mockkObject(ReminderManager)

        mockkStatic("androidx.glance.appwidget.GlanceAppWidgetKt")
        coEvery { any<MovieWidget>().updateAll(any()) } coAnswers { }

        every { AppCompatDelegate.setApplicationLocales(any()) } returns Unit
        every { AppCompatDelegate.setDefaultNightMode(any()) } returns Unit
        every { ReminderManager.scheduleReminder(any(), any(), any(), any()) } returns Unit
        every { ReminderManager.cancelReminder(any(), any()) } returns Unit

        every { settingsRepository.context } returns mockContext
        every { mockContext.resources } returns mockResources
        every { mockResources.configuration } returns mockConfiguration
        every { mockResources.displayMetrics } returns DisplayMetrics()

        every { settingsRepository.getSavedLanguage() } returns "ru"
        every { settingsRepository.getSavedTextScale() } returns TextScale.MEDIUM
        every { settingsRepository.getSavedTheme() } returns Theme.DARK
        every { settingsRepository.getFavoriteIds() } returns setOf("101")
        every { settingsRepository.getMovieTag(any()) } returns null
        every { settingsRepository.getMovieTag("101") } returns "Смотрю"
        every { settingsRepository.getMovieRating(any()) } returns -1
        every { settingsRepository.getMovieRating("101") } returns 5

        every { settingsRepository.addFavorite(any()) } returns Unit
        every { settingsRepository.removeFavorite(any()) } returns Unit
        every { settingsRepository.saveMovieTag(any(), any()) } returns Unit
        every { settingsRepository.saveMovieRating(any(), any()) } returns Unit
        every { settingsRepository.saveLanguage(any()) } returns Unit
        every { settingsRepository.saveTextScale(any()) } returns Unit
        every { settingsRepository.saveTheme(any()) } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(AppCompatDelegate::class)
        unmockkStatic(MovieWidget::class)
        unmockkStatic("androidx.glance.appwidget.GlanceAppWidgetKt")
        unmockkObject(ReminderManager)
    }

    @Test
    fun `initialization loads tags and ratings for favorites correctly`() {
        val viewModel = SettingsViewModel(settingsRepository, moviesRepository)

        assertEquals(setOf("101"), viewModel.favoriteIds.value)
        assertEquals("Смотрю", viewModel.movieTags.value["101"])
        assertEquals(5, viewModel.movieRatings.value["101"])
    }

    @Test
    fun `toggleFavorite adds movie if not present`() = runTest {
        val viewModel = SettingsViewModel(settingsRepository, moviesRepository)
        val newMovie = mockk<Movie> { every { id } returns 202 }

        viewModel.toggleFavorite(newMovie)

        assertTrue(viewModel.favoriteIds.value.contains("202"))
        coVerify { settingsRepository.addFavorite(202) }
        coVerify { moviesRepository.insertFavorite(newMovie) }
    }

    @Test
    fun `updateMovieTag schedules reminder if tag is Remind`() = runTest {
        val viewModel = SettingsViewModel(settingsRepository, moviesRepository)

        viewModel.updateMovieTag(
            id = 303,
            tag = "Напомнить",
            title = "Inception",
            releaseDate = "2010-07-16"
        )

        assertEquals("Напомнить", viewModel.movieTags.value["303"])
        coVerify { settingsRepository.saveMovieTag("303", "Напомнить") }
    }

    @Test
    fun `selectTheme updates configuration and persistence`() {
        val viewModel = SettingsViewModel(settingsRepository, moviesRepository)

        viewModel.selectTheme(Theme.LIGHT)

        assertEquals(Theme.LIGHT, viewModel.selectedTheme.value)
        coVerify { settingsRepository.saveTheme(Theme.LIGHT) }
    }
}