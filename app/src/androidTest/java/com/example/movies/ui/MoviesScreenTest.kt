package com.example.movies.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.movies.model.Movie
import com.example.movies.viewModel.MoviesUiState
import com.example.movies.viewModel.SettingsViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class MoviesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockSettingsViewModel: SettingsViewModel = mockk(relaxed = true) {
        every { favoriteIds } returns MutableStateFlow(setOf("101"))
        every { movieTags } returns MutableStateFlow(mapOf("101" to "Смотрю"))
        every { movieRatings } returns MutableStateFlow(mapOf("101" to 9))
    }

    @Test
    fun moviesScreen_successState_displaysMoviesWithTagsAndRatings() {
        // Arrange
        val testMovies = listOf(
            Movie(
                id = 101,
                title = "Интерстеллар",
                overview = "Фильм про космос и время...",
                posterPath = "/path.jpg",
                voteAverage = 8.6
            )
        )
        val successState = MoviesUiState.Success(movies = testMovies)

        // Act
        composeTestRule.setContent {
            MoviesScreen(
                moviesUiState = successState,
                retryAction = {},
                onLoadMore = {},
                onShowMovieDetails = {},
                settingsViewModel = mockSettingsViewModel
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Интерстеллар").assertIsDisplayed()
        composeTestRule.onNodeWithText("Фильм про космос и время...").assertIsDisplayed()

        composeTestRule.onNodeWithText("Смотрю").assertIsDisplayed()
        composeTestRule.onNodeWithText("9/10").assertIsDisplayed()
    }
}