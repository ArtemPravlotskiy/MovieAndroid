package com.example.movies.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.example.movies.R
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ErrorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun errorScreen_displaysMessageAndTriggersRetry() {
        var retryCalled = false
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val failedLoadText = context.getString(R.string.failed_load)
        val retryText = context.getString(R.string.retry)

        composeTestRule.setContent {
            ErrorScreen(
                retryAction = { retryCalled = true }
            )
        }

        composeTestRule.onNodeWithText(failedLoadText).assertIsDisplayed()

        composeTestRule.onNodeWithText(retryText).performClick()

        assertTrue(retryCalled)
    }
}