package com.example.movies

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MovieNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Test
    fun movieApp_startDestination_isStartScreen() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            MovieApp(navController = navController)
        }

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(MovieScreen.Start.name, route)
    }

    @Test
    fun movieApp_clickStart_navigatesToGenresScreen() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            MovieApp(navController = navController)
        }

        composeTestRule.onNodeWithText("S t a r t", ignoreCase = true).performClick()

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(MovieScreen.Genres.name, route)
    }

    @Test
    fun movieApp_clickAiChatIconInTopBar_navigatesToChatScreen() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            MovieApp(navController = navController)
        }

        composeTestRule.onNodeWithText("S t a r t", ignoreCase = true).performClick()

        val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        val aiChatContentDescription = context.getString(R.string.ai_chat)

        composeTestRule.onNodeWithContentDescription(aiChatContentDescription).performClick()

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(MovieScreen.AiChat.name, route)
    }
}