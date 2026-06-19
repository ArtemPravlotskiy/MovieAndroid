package com.example.movies.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.movies.model.MovieDetails
import com.example.movies.viewModel.MovieDetailsUiState
import com.example.movies.viewModel.SettingsViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class MovieDetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockSettingsViewModel: SettingsViewModel = mockk(relaxed = true)

    @Test
    fun movieDetails_isFavorite_clickTagButton_opensTagDialog() {
        val movieId = 550

        every { mockSettingsViewModel.favoriteIds } returns MutableStateFlow(setOf(movieId.toString()))
        every { mockSettingsViewModel.movieTags } returns MutableStateFlow(mapOf(movieId.toString() to "В планах"))
        every { mockSettingsViewModel.movieRatings } returns MutableStateFlow(emptyMap())

        val mockDetails = MovieDetails(
            id = movieId,
            title = "Бойцовский клуб",
            overview = "Сотрудник страховой компании...",
            posterPath = "/path.jpg",
            backdropPath = "/back.jpg",
            releaseDate = "1999-10-15",
            runtime = 139,
            voteAverage = 8.8,
            tagline = "Игры кончились",
            genres = listOf(com.example.movies.model.Genre(18, "Драма")),
            imdbId = "tt0137523"
        )

        composeTestRule.setContent {
            MovieDetailsScreen(
                movieDetailsUiState = MovieDetailsUiState.Success(mockDetails),
                retryAction = {},
                settingsViewModel = mockSettingsViewModel
            )
        }

        composeTestRule.onNodeWithText("В планах").assertIsDisplayed()

        composeTestRule.onNodeWithText("В планах").performClick()

        composeTestRule.onNodeWithText("Выберите тег").assertIsDisplayed()
        composeTestRule.onNodeWithText("Смотрю").assertIsDisplayed()
        composeTestRule.onNodeWithText("Смотрел").assertIsDisplayed()
    }
}