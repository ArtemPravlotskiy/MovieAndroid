package com.example.movies.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.movies.viewModel.SearchViewModel
import com.example.movies.viewModel.SettingsViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class SearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockSearchViewModel: SearchViewModel = mockk(relaxed = true)
    private val mockSettingsViewModel: SettingsViewModel = mockk(relaxed = true)

    private val isVectorSearchFlow = MutableStateFlow(false)
    private val searchResultFlow = MutableStateFlow(emptyList<com.example.movies.model.Movie>())

    private fun setupViewModels() {
        every { mockSearchViewModel.isVectorSearch } returns isVectorSearchFlow
        every { mockSearchViewModel.searchResult } returns searchResultFlow

        every { mockSettingsViewModel.favoriteIds } returns MutableStateFlow(emptySet())
        every { mockSettingsViewModel.movieTags } returns MutableStateFlow(emptyMap())
        every { mockSettingsViewModel.movieRatings } returns MutableStateFlow(emptyMap())
    }

    @Test
    fun searchScreen_defaultMode_showsTitlePlaceholder() {
        setupViewModels()

        composeTestRule.setContent {
            SearchScreen(
                viewModel = mockSearchViewModel,
                onShowMovieDetails = {},
                settingsViewModel = mockSettingsViewModel
            )
        }

        composeTestRule.onNodeWithText("Название фильма...").assertIsDisplayed()
    }

    @Test
    fun searchScreen_toggleMode_changesUiToVectorSearch() {
        setupViewModels()

        composeTestRule.setContent {
            SearchScreen(
                viewModel = mockSearchViewModel,
                onShowMovieDetails = {},
                settingsViewModel = mockSettingsViewModel
            )
        }

        every { mockSearchViewModel.toggleSearchMode() } answers {
            isVectorSearchFlow.value = true
        }

        composeTestRule.onNodeWithContentDescription("AI Search").performClick()

        composeTestRule.onNodeWithText("AI").assertIsDisplayed()
        composeTestRule.onNodeWithText("Опишите фильм...").assertIsDisplayed()
        composeTestRule.onNodeWithText("Включен поиск по смыслу (AI)").assertIsDisplayed()
    }
}