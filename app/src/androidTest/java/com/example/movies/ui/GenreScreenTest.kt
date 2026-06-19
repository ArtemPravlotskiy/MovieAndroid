package com.example.movies.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.movies.model.GenreDTO
import com.example.movies.viewModel.GenresUiState
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class GenreScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun genreScreen_successState_displaysGenres() {
        // Arrange
        val testGenres = listOf(
            GenreDTO(id = 28, name = "Action", imageName = "action"),
            GenreDTO(id = 35, name = "Comedy", imageName = "comedy")
        )
        val successState = GenresUiState.Success(genres = testGenres)

        // Act
        composeTestRule.setContent {
            GenreScreen(
                genresUiState = successState,
                retryAction = {},
                showMovieList = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Action").assertIsDisplayed()
        composeTestRule.onNodeWithText("Comedy").assertIsDisplayed()
    }

    @Test
    fun genreScreen_clickGenre_triggersCallbackWithCorrectId() {
        // Arrange
        val testGenres = listOf(GenreDTO(id = 28, name = "Action", imageName = "action"))
        var clickedGenreId: Int? = null

        composeTestRule.setContent {
            GenreScreen(
                genresUiState = GenresUiState.Success(genres = testGenres),
                retryAction = {},
                showMovieList = { id -> clickedGenreId = id }
            )
        }

        // Act
        composeTestRule.onNodeWithText("Action").performClick()

        // Assert
        Assert.assertEquals(28, clickedGenreId)
    }
}