package com.example.movies.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.movies.data.TextScale
import com.example.movies.data.Theme
import com.example.movies.viewModel.SettingsViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockSettingsViewModel: SettingsViewModel = mockk(relaxed = true)

    @Test
    fun settingsScreen_displaysOptionsAndTriggersThemeChange() {
        // Arrange
        every { mockSettingsViewModel.selectedLanguage } returns MutableStateFlow("en")
        every { mockSettingsViewModel.selectedScale } returns MutableStateFlow(TextScale.MEDIUM)
        every { mockSettingsViewModel.selectedTheme } returns MutableStateFlow(Theme.LIGHT)

        composeTestRule.setContent {
            SettingsScreen(
                viewModel = mockSettingsViewModel
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Theme").assertIsDisplayed()
        composeTestRule.onNodeWithText("Light").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dark").assertIsDisplayed()

        // Act
        composeTestRule.onNodeWithText("Dark").performClick()

        // Assert
        verify(exactly = 1) { mockSettingsViewModel.selectTheme(Theme.DARK) }
    }
}